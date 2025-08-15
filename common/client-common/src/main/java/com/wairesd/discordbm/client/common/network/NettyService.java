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
    private static final String RESPONSE_TYPE = "response";
    private static final String INVALID_HOST_MESSAGE = "Invalid host host or port configuration. Check your settings.yml.";
    private static final String CLIENT_NOT_ACTIVE_MESSAGE = "Netty client not active.";

    private final Supplier<Platform> platformSupplier;
    private final Gson gson = new Gson();
    private final PluginLogger pluginLogger;
    private NettyClient nettyClient;

    public NettyService(Supplier<Platform> platformSupplier, PluginLogger pluginLogger) {
        this.platformSupplier = platformSupplier;
        this.pluginLogger = pluginLogger;
    }

    public void initializeNettyClient() {
        Platform platform = getPlatform();

        if (!isHostConfigurationValid(platform)) {
            logInvalidHostConfiguration();
            return;
        }

        createAndConnectClient(platform);
    }

    public void closeNettyConnection() {
        if (isClientActive()) {
            performConnectionClose();
        }
    }

    public void sendResponse(String requestId, String embedJson) {
        if (isClientActive()) {
            processAndSendResponse(requestId, embedJson);
        } else {
            logClientNotActive("Response not sent.");
        }
    }

    public void sendNettyMessage(String message) {
        if (isClientActive()) {
            nettyClient.send(message);
        } else {
            logMessageNotSent(message);
        }
    }

    public void registerCommands(List<Command> commands) {
        if (isClientActive()) {
            nettyClient.registerCommands(commands);
        } else {
            logClientNotActive("Commands not registered.");
        }
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }

    private boolean isClientActive() {
        return nettyClient != null && nettyClient.isActive();
    }

    private Platform getPlatform() {
        return platformSupplier.get();
    }

    private boolean isHostConfigurationValid(Platform platform) {
        String host = platform.getHostIp();
        int port = platform.getHostPort();
        return host != null && !host.isEmpty() && port > 0;
    }

    private void createAndConnectClient(Platform platform) {
        InetSocketAddress address = createSocketAddress(platform);
        nettyClient = new NettyClient(address, platform, pluginLogger);
        attemptConnection(platform);
    }

    private InetSocketAddress createSocketAddress(Platform platform) {
        return new InetSocketAddress(platform.getHostIp(), platform.getHostPort());
    }

    private void attemptConnection(Platform platform) {
        try {
            nettyClient.connect();
        } catch (Exception e) {
            handleConnectionException(platform, e);
        }
    }

    private void performConnectionClose() {
        nettyClient.close();
        nettyClient = null;
        logConnectionClosed();
    }

    private void processAndSendResponse(String requestId, String embedJson) {
        try {
            EmbedDefinition embedObj = parseEmbedJson(embedJson);
            ResponseMessage respMsg = buildResponseMessage(requestId, embedObj);
            String jsonMessage = gson.toJson(respMsg);
            nettyClient.send(jsonMessage);
        } catch (Exception e) {
            logEmbedParsingError(e);
        }
    }

    private EmbedDefinition parseEmbedJson(String embedJson) {
        return gson.fromJson(embedJson, EmbedDefinition.class);
    }

    private ResponseMessage buildResponseMessage(String requestId, EmbedDefinition embedObj) {
        return new ResponseMessage.Builder()
                .type(RESPONSE_TYPE)
                .requestId(requestId)
                .response(null)
                .embed(embedObj)
                .buttons(null)
                .build();
    }

    // Logging methods
    private void logInvalidHostConfiguration() {
        pluginLogger.warn(INVALID_HOST_MESSAGE);
    }

    private void handleConnectionException(Platform platform, Exception e) {
        if (platform.isDebugErrors()) {
            pluginLogger.warn("Failed to connect to host Netty server: " + e.getMessage());
        }
    }

    private void logConnectionClosed() {
        Platform platform = getPlatform();
        if (platform.isDebugConnections()) {
            pluginLogger.info("Netty connection closed.");
        }
    }

    private void logClientNotActive(String action) {
        pluginLogger.warn(CLIENT_NOT_ACTIVE_MESSAGE + " " + action);
    }

    private void logMessageNotSent(String message) {
        Platform platform = getPlatform();
        if (platform.isDebugErrors()) {
            pluginLogger.warn("Netty connection not active. Message not sent: " + message);
        }
    }

    private void logEmbedParsingError(Exception e) {
        pluginLogger.warn("Failed to parse embedJson for sendResponse: " + e.getMessage());
    }
}