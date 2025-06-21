package com.wairesd.discordbm.client.common.network;

import com.google.gson.Gson;
import com.wairesd.discordbm.client.common.models.command.Command;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

public class NettyService {
    private final Supplier<Platform> platformSupplier;
    private final Gson gson = new Gson();
    private final PluginLogger pluginLogger;
    private NettyClient nettyClient;

    public NettyService(Supplier<Platform> platformSupplier, PluginLogger pluginLogger) {
        this.platformSupplier = platformSupplier;
        this.pluginLogger = pluginLogger;
    }

    private boolean isClientActive() {
        return nettyClient != null && nettyClient.isActive();
    }

    public void initializeNettyClient() {
        Platform platform = platformSupplier.get();
        String host = platform.getVelocityHost();
        int port = platform.getVelocityPort();
        
        if (host == null || host.isEmpty() || port <= 0) {
            pluginLogger.warn("Invalid Velocity host or port configuration. Check your settings.yml.");
            return;
        }
        
        nettyClient = new NettyClient(new InetSocketAddress(host, port), platform, pluginLogger);
        try {
            nettyClient.connect();
        } catch (Exception e) {
            if (platform.isDebugErrors()) {
                pluginLogger.warn("Failed to connect to Velocity Netty server: " + e.getMessage());
            }
        }
    }

    public void closeNettyConnection() {
        if (isClientActive()) {
            nettyClient.close();
            nettyClient = null;
            Platform platform = platformSupplier.get();
            if (platform.isDebugConnections()) {
                pluginLogger.info("Netty connection closed.");
            }
        }
    }

    public void sendResponse(String requestId, String embedJson) {
        if (isClientActive()) {
            try {
                EmbedDefinition embedObj = gson.fromJson(embedJson, EmbedDefinition.class);
                ResponseMessage respMsg = new ResponseMessage.Builder()
                        .type("response")
                        .requestId(requestId)
                        .response(null)
                        .embed(embedObj)
                        .buttons(null)
                        .build();
                nettyClient.send(gson.toJson(respMsg));
            } catch (Exception e) {
                pluginLogger.warn("Failed to parse embedJson for sendResponse: " + e.getMessage());
            }
        } else {
            pluginLogger.warn("Netty client not active. Response not sent.");
        }
    }

    public void sendNettyMessage(String message) {
        if (isClientActive()) {
            nettyClient.send(message);
        } else {
            Platform platform = platformSupplier.get();
            if (platform.isDebugErrors()) {
                pluginLogger.warn("Netty connection not active. Message not sent: " + message);
            }
        }
    }

    public void registerCommands(List<Command> commands) {
        if (isClientActive()) {
            nettyClient.registerCommands(commands);
        } else {
            pluginLogger.warn("Netty client not active. Commands not registered.");
        }
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }
}