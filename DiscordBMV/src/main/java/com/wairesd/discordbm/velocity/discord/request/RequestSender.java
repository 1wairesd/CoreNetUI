package com.wairesd.discordbm.velocity.discord.request;

import com.google.gson.Gson;
import com.wairesd.discordbm.velocity.models.request.RequestMessage;
import com.wairesd.discordbm.velocity.network.NettyServer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RequestSender {
    private static final Gson GSON = new Gson();

    private final NettyServer nettyServer;
    private final Logger logger;

    private final ConcurrentHashMap<UUID, SlashCommandInteractionEvent> pendingRequests = new ConcurrentHashMap<>();

    public RequestSender(NettyServer nettyServer, Logger logger) {
        this.nettyServer = nettyServer;
        this.logger = logger;
    }

    public void sendRequestToServer(SlashCommandInteractionEvent event, NettyServer.ServerInfo serverInfo) {
        UUID requestId = generateRequestId();
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

    private RequestMessage createRequestMessage(SlashCommandInteractionEvent event, UUID requestId) {
        Map<String, String> options = event.getOptions().stream()
                .collect(Collectors.toMap(opt -> opt.getName(), opt -> opt.getAsString()));
        return new RequestMessage("request", event.getName(), options, requestId.toString());
    }

    private void logDebug(String message, String serverName) {
        if (com.wairesd.discordbm.velocity.config.configurators.Settings.isDebugClientResponses()) {
            logger.info("Sending request to server {}: {}", serverName, message);
        }
    }

    public ConcurrentHashMap<UUID, SlashCommandInteractionEvent> getPendingRequests() {
        return pendingRequests;
    }
}
