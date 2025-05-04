package com.wairesd.discordbm.velocity.commands.commandbuilder.actions;

import com.google.gson.Gson;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.config.configurators.Messages;
import com.wairesd.discordbm.velocity.network.NettyServer;
import com.wairesd.discordbm.common.models.placeholders.request.CanHandlePlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.request.GetPlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;

import io.netty.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResolvePlaceholdersAction implements CommandAction {
    private final String template;
    private final String playerTemplate;
    private final DiscordBMV plugin;
    private final NettyServer nettyServer;

    public ResolvePlaceholdersAction(Map<String, Object> properties, DiscordBMV plugin) {
        this.template = (String) properties.get("template");
        this.playerTemplate = (String) properties.get("player");
        this.plugin = plugin;
        this.nettyServer = plugin.getNettyServer();
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        NettyServer nettyServer = plugin.getNettyServer();
        if (nettyServer == null) {
            context.setResolvedMessage("NettyServer is not initialized.");
            return CompletableFuture.completedFuture(null);
        }
        SlashCommandInteractionEvent event = context.getEvent();
        String playerName = formatMessage(event, playerTemplate);
        List<String> placeholders = extractPlaceholders(template);
        var proxy = plugin.getProxy();
        var playerOpt = proxy.getPlayer(playerName);

        if (playerOpt.isPresent()) {
            var player = playerOpt.get();
            var serverOpt = player.getCurrentServer();
            if (serverOpt.isPresent()) {
                String serverName = serverOpt.get().getServerInfo().getName();
                Channel channel = findChannelForServer(serverName, nettyServer);
                if (channel != null) {
                    String requestId = UUID.randomUUID().toString();
                    GetPlaceholdersRequest req = new GetPlaceholdersRequest("get_placeholders", playerName, placeholders, requestId);
                    String json = new Gson().toJson(req);
                    nettyServer.sendMessage(channel, json);
                    CompletableFuture<PlaceholdersResponse> future = new CompletableFuture<>();
                    nettyServer.getPlaceholderFutures().put(requestId, future);
                    return future.thenAccept(resp -> {
                        Map<String, String> values = resp.values();
                        String resolved = substitutePlaceholders(template, values);
                        plugin.getLogger().info("Resolved message: {}", resolved);
                        context.setResolvedMessage(resolved);
                    }).exceptionally(ex -> {
                        context.setResolvedMessage("Error getting placeholders: " + ex.getMessage());
                        return null;
                    });
                } else {
                    context.setResolvedMessage("The server is not connected.");
                    return CompletableFuture.completedFuture(null);
                }
            } else {
                context.setResolvedMessage("The player is online, but not connected to the server.");
                return CompletableFuture.completedFuture(null);
            }
        } else {
            List<Channel> channels = new ArrayList<>(nettyServer.getChannelToServerName().keySet());
            Map<String, CompletableFuture<Boolean>> futures = new HashMap<>();
            for (Channel channel : channels) {
                String serverName = nettyServer.getServerName(channel);
                String requestId = UUID.randomUUID().toString();
                CanHandlePlaceholdersRequest req = new CanHandlePlaceholdersRequest("can_handle_placeholders", playerName, placeholders, requestId);
                String json = new Gson().toJson(req);
                nettyServer.sendMessage(channel, json);
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                nettyServer.getCanHandleFutures().put(requestId, future);
                futures.put(serverName, future);
            }
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0]));
            return allFutures.orTimeout(5, TimeUnit.SECONDS).thenCompose(v -> {
                List<String> capableServers = new ArrayList<>();
                for (var entry : futures.entrySet()) {
                    try {
                        boolean canHandle = entry.getValue().get();
                        if (canHandle) {
                            capableServers.add(entry.getKey());
                        }
                    } catch (Exception e) {
                    }
                }
                if (capableServers.isEmpty()) {
                    context.setResolvedMessage("No server can handle the required placeholders.");
                    return CompletableFuture.completedFuture(null);
                } else {
                    String serverName = capableServers.get(0);
                    Channel channel = findChannelForServer(serverName, nettyServer);
                    if (channel != null) {
                        String requestId = UUID.randomUUID().toString();
                        GetPlaceholdersRequest req = new GetPlaceholdersRequest("get_placeholders", playerName, placeholders, requestId);
                        String json = new Gson().toJson(req);
                        nettyServer.sendMessage(channel, json);
                        CompletableFuture<PlaceholdersResponse> future = new CompletableFuture<>();
                        nettyServer.getPlaceholderFutures().put(requestId, future);
                        return future.thenAccept(resp -> {
                            Map<String, String> values = resp.values();
                            String resolved = substitutePlaceholders(template, values);
                            plugin.getLogger().info("Resolved message: {}", resolved);
                            context.setResolvedMessage(resolved);
                        }).exceptionally(ex -> {
                            context.setResolvedMessage("Error getting placeholders: " + ex.getMessage());
                            return null;
                        });
                    } else {
                        context.setResolvedMessage("The selected server is not connected.");
                        return CompletableFuture.completedFuture(null);
                    }
                }
            });
        }
    }

    private String formatMessage(SlashCommandInteractionEvent event, String template) {
        String result = template;
        for (var option : event.getOptions()) {
            String placeholder = "{" + option.getName() + "}";
            result = result.replace(placeholder, option.getAsString());
        }
        return result;
    }

    private List<String> extractPlaceholders(String template) {
        List<String> placeholders = new ArrayList<>();
        Pattern pattern = Pattern.compile("%([^%]+)%");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            placeholders.add(matcher.group());
        }
        return placeholders;
    }

    private String substitutePlaceholders(String template, Map<String, String> values) {
        String result = template;
        String offlineMessage = Messages.getMessage("offline-player", null);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = entry.getKey();
            String value       = entry.getValue();
            boolean isEmptyOrUnchanged = (value == null)
                    || value.isEmpty()
                    || value.equals(placeholder);
            String replacement = isEmptyOrUnchanged
                    ? (offlineMessage != null ? offlineMessage : Messages.DEFAULT_MESSAGE)
                    : value;
            result = result.replace(placeholder, replacement);
        }
        return result;
    }


    private Channel findChannelForServer(String serverName, NettyServer nettyServer) {
        for (var entry : nettyServer.getChannelToServerName().entrySet()) {
            if (entry.getValue().equals(serverName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}