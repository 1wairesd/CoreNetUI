package com.wairesd.discordbm.host.common.discord.request;

import com.google.gson.Gson;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.models.request.RequestMessage;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RequestSender {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final Gson GSON = new Gson();
    private final NettyServer nettyServer;
    private final ConcurrentHashMap<UUID, SlashCommandInteractionEvent> pendingRequests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, InteractionHook> pendingHooks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> requestServerNames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Boolean> requestEphemeral = new ConcurrentHashMap<>();
    
    public static final String SERVER_NAME_VAR = "discordbm_server_name";

    public RequestSender(NettyServer nettyServer, PluginLogger logger) {
        this.nettyServer = nettyServer;
    }

    public void sendRequestToServer(SlashCommandInteractionEvent event, NettyServer.ServerInfo serverInfo, boolean requiresModal, boolean useDeferReply, UUID requestId, boolean ephemeral) {
        requestEphemeral.put(requestId, ephemeral);
        if (requiresModal) {
            pendingRequests.put(requestId, event);
            requestServerNames.put(requestId, serverInfo.serverName());
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Added requestId {} to pendingRequests for modal", requestId);
            }
            RequestMessage request = createRequestMessage(event, requestId, ephemeral);
            String json = GSON.toJson(request);
            nettyServer.sendMessage(serverInfo.channel(), json);
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Sent request for requestId {} (modal)", requestId);
            }
            return;
        }
        if (useDeferReply) {
            pendingRequests.put(requestId, event);
            requestServerNames.put(requestId, serverInfo.serverName());
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Added requestId {} to pendingRequests after defer (no second defer)", requestId);
            }
            RequestMessage request = createRequestMessage(event, requestId, ephemeral);
            String json = GSON.toJson(request);
            nettyServer.sendMessage(serverInfo.channel(), json);
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Sent request for requestId {} (after defer, no second defer)", requestId);
            }
            return;
        }
        pendingRequests.put(requestId, event);
        requestServerNames.put(requestId, serverInfo.serverName());
        if (Settings.isDebugRequestProcessing()) {
            logger.info("Added requestId {} to pendingRequests (no defer)", requestId);
        }
        RequestMessage request = createRequestMessage(event, requestId, ephemeral);
        String json = GSON.toJson(request);
        nettyServer.sendMessage(serverInfo.channel(), json);
        if (Settings.isDebugRequestProcessing()) {
            logger.info("Sent request for requestId {} (no defer)", requestId);
        }
    }

    public void storeInteractionHook(UUID requestId, InteractionHook hook) {
        pendingHooks.put(requestId, hook);
        if (Settings.isDebugRequestProcessing()) {
            logger.info("Stored interaction hook for requestId {}", requestId);
        }
    }

    public InteractionHook removeInteractionHook(UUID requestId) {
        return pendingHooks.remove(requestId);
    }

    private RequestMessage createRequestMessage(SlashCommandInteractionEvent event, UUID requestId, boolean ephemeral) {
        Map<String, String> options = event.getOptions().stream()
                .collect(Collectors.toMap(opt -> opt.getName(), opt -> opt.getAsString()));
        options.put("user_Id", event.getUser().getId());
        if (event.getGuild() != null) {
            options.put("guild_Id", event.getGuild().getId());
            
            var member = event.getMember();
            if (member != null && !member.getRoles().isEmpty()) {
                String roleId = member.getRoles().get(0).getId();
                options.put("role_id", roleId);
            }
        }
        options.put("ephemeral", Boolean.toString(ephemeral));
        options.put("channelId", event.getChannel().getId());
        return new RequestMessage("request", event.getName(), options, requestId.toString());
    }

    public ConcurrentHashMap<UUID, SlashCommandInteractionEvent> getPendingRequests() {
        return pendingRequests;
    }

    public String getServerNameForRequest(UUID requestId) {
        return requestServerNames.get(requestId);
    }

    public void storeServerNameForRequest(UUID requestId, String serverName) {
        requestServerNames.put(requestId, serverName);
    }
}
