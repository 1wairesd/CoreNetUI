package com.wairesd.discordbm.client.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.common.network.codec.ByteBufDecoder;
import com.wairesd.discordbm.common.network.codec.ByteBufEncoder;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.client.common.handler.MessageHandler;
import com.wairesd.discordbm.client.common.models.command.Command;
import com.wairesd.discordbm.common.models.register.ClientRegisterMessage;
import com.wairesd.discordbm.common.models.register.RegisterMessage;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.models.response.ResponseFlags;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NettyClient {
    private static final int MAX_FRAME_LENGTH = 65535;
    private static final int LENGTH_FIELD_LENGTH = 2;
    private static final int LENGTH_FIELD_OFFSET = 0;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 2;

    private final PluginLogger pluginLogger;
    private final InetSocketAddress address;
    private final Platform platform;
    private final Gson gson = new Gson();

    private EventLoopGroup group;
    private Channel channel;
    private Runnable onConnectionFailure;

    public NettyClient(InetSocketAddress address, Platform platform, PluginLogger pluginLogger) {
        this.address = address;
        this.platform = platform;
        this.pluginLogger = pluginLogger;
    }

    public void setOnConnectionFailure(Runnable onConnectionFailure) {
        this.onConnectionFailure = onConnectionFailure;
    }

    public void connect() {
        CompletableFuture.runAsync(this::executeConnection)
                .exceptionally(this::handleConnectionException);
    }

    public void close() {
        closeChannel();
        shutdownEventLoopGroup();
        logConnectionClosed();
    }

    public void registerCommands(List<Command> commands) {
        if (isCommandListEmpty(commands)) {
            return;
        }

        String json = createCommandRegistrationMessage(commands);
        send(json);
        logCommandRegistration(commands);
    }

    public void send(String message) {
        if (isActive()) {
            String processedMessage = autoDetectAndUpdateResponseType(message);
            channel.writeAndFlush(processedMessage);
        } else {
            logInactiveChannelWarning(message);
        }
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }

    private void executeConnection() {
        initializeEventLoopGroup();
        Bootstrap bootstrap = createBootstrap();

        try {
            ChannelFuture future = bootstrap.connect(address).sync();
            handleConnectionResult(future);
        } catch (InterruptedException e) {
            handleInterruptedException(e);
        }
    }

    private void initializeEventLoopGroup() {
        group = new NioEventLoopGroup();
    }

    private Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        return bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(createChannelInitializer());
    }

    private ChannelInitializer<SocketChannel> createChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                setupChannelPipeline(ch);
            }
        };
    }

    private void setupChannelPipeline(SocketChannel ch) {
        ch.pipeline().addLast("frameDecoder", createFrameDecoder());
        ch.pipeline().addLast("byteBufDecoder", new ByteBufDecoder());
        ch.pipeline().addLast("frameEncoder", createFrameEncoder());
        ch.pipeline().addLast("byteBufEncoder", new ByteBufEncoder());
        ch.pipeline().addLast("handler", new MessageHandler(platform, pluginLogger));
    }

    private LengthFieldBasedFrameDecoder createFrameDecoder() {
        return new LengthFieldBasedFrameDecoder(
                MAX_FRAME_LENGTH,
                LENGTH_FIELD_OFFSET,
                LENGTH_FIELD_LENGTH,
                LENGTH_ADJUSTMENT,
                INITIAL_BYTES_TO_STRIP
        );
    }

    private LengthFieldPrepender createFrameEncoder() {
        return new LengthFieldPrepender(LENGTH_FIELD_LENGTH);
    }

    private void handleConnectionResult(ChannelFuture future) {
        if (future.isSuccess()) {
            handleSuccessfulConnection(future);
        } else {
            handleFailedConnection(future);
        }
    }

    private void handleSuccessfulConnection(ChannelFuture future) {
        channel = future.channel();
        logSuccessfulConnection();
        registerClient();
    }

    private void handleFailedConnection(ChannelFuture future) {
        logFailedConnection(future.cause());
        shutdownGroupAndNotify();
    }

    private void handleInterruptedException(InterruptedException e) {
        logInterruptedException(e);
        Thread.currentThread().interrupt();
        shutdownGroupAndNotify();
    }

    private Void handleConnectionException(Throwable throwable) {
        logConnectionException(throwable);
        shutdownGroupAndNotify();
        return null;
    }

    private void shutdownGroupAndNotify() {
        shutdownEventLoopGroup();
        notifyConnectionFailure();
    }

    private void shutdownEventLoopGroup() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    private void notifyConnectionFailure() {
        if (onConnectionFailure != null) {
            onConnectionFailure.run();
        }
    }

    private void closeChannel() {
        if (channel != null) {
            channel.close();
        }
    }

    private void registerClient() {
        String secretCode = platform.getSecretCode();
        if (isSecretCodeValid(secretCode)) {
            sendClientRegistrationMessage(secretCode);
            logClientRegistration();
        }
    }

    private boolean isSecretCodeValid(String secretCode) {
        return secretCode != null && !secretCode.isEmpty();
    }

    private void sendClientRegistrationMessage(String secretCode) {
        ClientRegisterMessage clientRegisterMsg = new ClientRegisterMessage(
                platform.getServerName(),
                secretCode
        );
        String json = gson.toJson(clientRegisterMsg);
        send(json);
    }

    private boolean isCommandListEmpty(List<Command> commands) {
        return commands == null || commands.isEmpty();
    }

    private String createCommandRegistrationMessage(List<Command> commands) {
        String secret = platform.getSecretCode();
        RegisterMessage<Command> registerMsg = new RegisterMessage.Builder<Command>()
                .type("register")
                .serverName(platform.getServerName())
                .pluginName("DiscordBM")
                .commands(commands)
                .secret(secret)
                .build();
        return gson.toJson(registerMsg);
    }

    private String autoDetectAndUpdateResponseType(String message) {
        try {
            JsonObject json = parseJsonMessage(message);
            String type = extractMessageType(json);

            if (isResponseMessage(type)) {
                return processResponseMessage(message);
            }
        } catch (Exception e) {
            logResponseTypeDetectionError(e);
        }

        return message;
    }

    private JsonObject parseJsonMessage(String message) {
        return new com.google.gson.JsonParser().parse(message).getAsJsonObject();
    }

    private String extractMessageType(JsonObject json) {
        return json.get("type") != null ? json.get("type").getAsString() : null;
    }

    private boolean isResponseMessage(String type) {
        return "response".equals(type);
    }

    private String processResponseMessage(String message) {
        ResponseMessage respMsg = gson.fromJson(message, ResponseMessage.class);
        ResponseTypeDetector.ResponseType responseType = ResponseTypeDetector.determineResponseType(respMsg);
        ResponseFlags updatedFlags = ResponseTypeDetector.updateFlagsForResponseType(respMsg, responseType);

        ResponseMessage updatedRespMsg = buildUpdatedResponseMessage(respMsg, updatedFlags);
        logResponseTypeDetection(responseType);

        return gson.toJson(updatedRespMsg);
    }

    private ResponseMessage buildUpdatedResponseMessage(ResponseMessage respMsg, ResponseFlags updatedFlags) {
        return new ResponseMessage.Builder()
                .type(respMsg.type())
                .requestId(respMsg.requestId())
                .response(respMsg.response())
                .embed(respMsg.embed())
                .buttons(respMsg.buttons())
                .modal(respMsg.modal())
                .flags(updatedFlags)
                .userId(respMsg.userId())
                .channelId(respMsg.channelId())
                .conditions(respMsg.conditions())
                .responses(respMsg.responses())
                .replyMessageId(respMsg.replyMessageId())
                .replyMentionAuthor(respMsg.replyMentionAuthor())
                .build();
    }

    // Logging methods
    private void logSuccessfulConnection() {
        if (platform.isDebugConnections()) {
            pluginLogger.info("Connected to host at " + address.getHostString() + ":" + address.getPort());
        }
    }

    private void logFailedConnection(Throwable cause) {
        if (platform.isDebugConnections() || platform.isDebugErrors()) {
            String errorMessage = cause != null ? cause.getMessage() : "Unknown error";
            pluginLogger.warn("Failed to connect to host at " + address.getHostString() + ":"
                    + address.getPort() + ": " + errorMessage);
        } else {
            pluginLogger.warn("Failed to connect to host server. Check your settings.yml configuration.");
        }
    }

    private void logInterruptedException(InterruptedException e) {
        if (platform.isDebugErrors()) {
            pluginLogger.error("Connection interrupted: " + e.getMessage());
        }
    }

    private void logConnectionException(Throwable throwable) {
        if (platform.isDebugErrors()) {
            String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
            pluginLogger.error("Error connecting to host: " + errorMessage);
        } else {
            pluginLogger.warn("Failed to connect to host: server. Check your settings.yml configuration.");
        }
    }

    private void logConnectionClosed() {
        if (platform.isDebugConnections()) {
            pluginLogger.info("Netty client connection closed");
        }
    }

    private void logClientRegistration() {
        if (platform.isDebugCommandRegistrations()) {
            pluginLogger.info("Sent client registration message.");
        }
    }

    private void logCommandRegistration(List<Command> commands) {
        if (platform.isDebugCommandRegistrations()) {
            String commandNames = commands.stream()
                    .map(Command::getName)
                    .collect(Collectors.joining(", "));
            pluginLogger.info("Sent register message with commands: " + commandNames);
        }
    }

    private void logResponseTypeDetection(ResponseTypeDetector.ResponseType responseType) {
        if (platform.isDebugCommandRegistrations()) {
            pluginLogger.info("Auto-detected response type: %s for message", responseType);
        }
    }

    private void logResponseTypeDetectionError(Exception e) {
        if (platform.isDebugErrors()) {
            pluginLogger.error("Error auto-detecting response type: " + e.getMessage());
        }
    }

    private void logInactiveChannelWarning(String message) {
        pluginLogger.warn("Netty channel not active. Message not sent: " + message);
    }
}