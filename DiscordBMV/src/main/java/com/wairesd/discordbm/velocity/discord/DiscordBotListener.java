package com.wairesd.discordbm.velocity.discord;

import com.google.gson.Gson;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.CommandExecutor;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.models.command.CommandDefinition;
import com.wairesd.discordbm.velocity.models.request.RequestMessage;
import com.wairesd.discordbm.velocity.network.NettyServer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DiscordBotListener extends ListenerAdapter {
    private static final Gson GSON = new Gson();
    private static final String SELECT_MENU_PREFIX = "select_server_";

    private final DiscordBMV plugin;
    private final NettyServer nettyServer;
    private final Logger logger;
    private final CommandExecutor commandExecutor;
    private final ConcurrentHashMap<UUID, SlashCommandInteractionEvent> pendingRequests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SelectionInfo> pendingSelections = new ConcurrentHashMap<>();

    public DiscordBotListener(DiscordBMV plugin, NettyServer nettyServer, Logger logger) {
        this.plugin = plugin;
        this.nettyServer = nettyServer;
        this.logger = logger;
        this.commandExecutor = new CommandExecutor();
    }

    public ConcurrentHashMap<UUID, SlashCommandInteractionEvent> getPendingRequests() {
        return pendingRequests;
    }

    public Map<String, SelectionInfo> getPendingSelections() {
        return pendingSelections;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        List<NettyServer.ServerInfo> servers = nettyServer.getServersForCommand(command);

        if (servers.isEmpty()) {
            handleCustomCommand(event, command);
            return;
        }

        CommandDefinition cmdDef = nettyServer.getCommandDefinitions().get(command);
        if (isCommandRestrictedToDM(event, cmdDef)) {
            replyCommandRestrictedToDM(event);
            return;
        }

        handleServerSelection(event, servers);
    }

    private boolean isCommandRestrictedToDM(SlashCommandInteractionEvent event, CommandDefinition cmdDef) {
        return "dm".equals(cmdDef.context()) && event.getGuild() != null;
    }

    private void replyCommandRestrictedToDM(SlashCommandInteractionEvent event) {
        event.reply("This command is only available in direct messages.")
                .setEphemeral(true)
                .queue();
    }

    private void handleServerSelection(SlashCommandInteractionEvent event, List<NettyServer.ServerInfo> servers) {
        if (servers.size() == 1) {
            sendRequestToSingleServer(event, servers.get(0));
        } else {
            sendServerSelectionMenu(event, servers);
        }
    }

    private void handleCustomCommand(SlashCommandInteractionEvent event, String command) {
        var customCommand = plugin.getCommandManager().getCommand(command);
        if (customCommand != null) {
            commandExecutor.execute(event, customCommand);
        } else {
            event.reply("Command unavailable.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private void sendRequestToSingleServer(SlashCommandInteractionEvent event, NettyServer.ServerInfo serverInfo) {
        sendRequestToServer(event, serverInfo, generateRequestId());
    }

    private void sendServerSelectionMenu(SlashCommandInteractionEvent event, List<NettyServer.ServerInfo> servers) {
        String selectMenuId = generateSelectMenuId();
        pendingSelections.put(selectMenuId, new SelectionInfo(event, servers));

        StringSelectMenu menu = createServerSelectMenu(selectMenuId, servers);

        event.reply("Command registered on multiple servers. Select one:")
                .addActionRow(menu)
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().startsWith(SELECT_MENU_PREFIX)) return;

        SelectionInfo selectionInfo = pendingSelections.remove(event.getComponentId());
        if (selectionInfo == null) {
            replySelectionTimeout(event);
            return;
        }

        handleSelectedServer(event, selectionInfo);
    }

    private void handleSelectedServer(StringSelectInteractionEvent event, SelectionInfo selectionInfo) {
        String chosenServerName = event.getValues().stream().findFirst().orElse(null);
        if (chosenServerName == null) {
            replyNoServerSelected(event);
            return;
        }

        NettyServer.ServerInfo targetServer = findTargetServer(selectionInfo.servers(), chosenServerName);
        if (targetServer == null) {
            replyServerNotFound(event);
            return;
        }

        sendRequestToSelectedServer(selectionInfo.event(), targetServer, chosenServerName);
    }

    private NettyServer.ServerInfo findTargetServer(List<NettyServer.ServerInfo> servers, String chosenServerName) {
        return servers.stream()
                .filter(server -> server.serverName().equals(chosenServerName))
                .findFirst()
                .orElse(null);
    }

    private void sendRequestToSelectedServer(SlashCommandInteractionEvent event, NettyServer.ServerInfo targetServer, String chosenServerName) {
        sendRequestToServer(event, targetServer, generateRequestId());
    }

    private void sendRequestToServer(SlashCommandInteractionEvent event, NettyServer.ServerInfo serverInfo, UUID requestId) {
        pendingRequests.put(requestId, event);
        event.deferReply().queue();

        RequestMessage request = createRequestMessage(event, requestId);
        String json = GSON.toJson(request);
        logDebug(json, serverInfo.serverName());
        nettyServer.sendMessage(serverInfo.channel(), json);
    }

    private UUID generateRequestId() {
        return UUID.randomUUID();
    }

    private String generateSelectMenuId() {
        return SELECT_MENU_PREFIX + UUID.randomUUID();
    }

    private StringSelectMenu createServerSelectMenu(String selectMenuId, List<NettyServer.ServerInfo> servers) {
        return StringSelectMenu.create(selectMenuId)
                .setPlaceholder("Select a server")
                .setRequiredRange(1, 1)
                .addOptions(servers.stream()
                        .map(server -> SelectOption.of(server.serverName(), server.serverName()))
                        .collect(Collectors.toList()))
                .build();
    }

    private RequestMessage createRequestMessage(SlashCommandInteractionEvent event, UUID requestId) {
        Map<String, String> options = event.getOptions().stream()
                .collect(Collectors.toMap(opt -> opt.getName(), opt -> opt.getAsString()));
        return new RequestMessage("request", event.getName(), options, requestId.toString());
    }

    private void replySelectionTimeout(StringSelectInteractionEvent event) {
        event.reply("Selection timeout expired.")
                .setEphemeral(true)
                .queue();
    }

    private void replyNoServerSelected(StringSelectInteractionEvent event) {
        event.reply("No server selected.")
                .setEphemeral(true)
                .queue();
    }

    private void replyServerNotFound(StringSelectInteractionEvent event) {
        event.reply("Selected server not found.")
                .setEphemeral(true)
                .queue();
    }

    private void logDebug(String message, String serverName) {
        if (Settings.isDebugClientResponses()) {
            logger.info("Sending request to server {}: {}", serverName, message);
        }
    }

    public record SelectionInfo(SlashCommandInteractionEvent event, List<NettyServer.ServerInfo> servers) {
    }
}
