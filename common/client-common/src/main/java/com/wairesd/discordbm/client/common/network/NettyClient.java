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
    private final PluginLogger pluginLogger;
    private final InetSocketAddress address;
    private final Platform platform;
    private EventLoopGroup group;
    private Channel channel;
    private final Gson gson = new Gson();
    private Runnable onConnectionFailure;

    public NettyClient(InetSocketAddress address, Platform platform, PluginLogger pluginLogger) {
        this.address = address;
        this.platform = platform;
        this.pluginLogger = pluginLogger;
    }

    private void shutdownGroupAndNotify() {
        if (group != null) group.shutdownGracefully();
        if (onConnectionFailure != null) onConnectionFailure.run();
    }

    public void connect() {
        CompletableFuture.runAsync(() -> {
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            ch.pipeline().addLast("byteBufDecoder", new ByteBufDecoder());
                            ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            ch.pipeline().addLast("byteBufEncoder", new ByteBufEncoder());
                            ch.pipeline().addLast("handler", new MessageHandler(platform, pluginLogger));
                        }
                    });
            try {
                ChannelFuture future = bootstrap.connect(address).sync();
                if (future.isSuccess()) {
                    channel = future.channel();
                    if (platform.isDebugConnections()) {
                        pluginLogger.info("Connected to host at " + address.getHostString() + ":" + address.getPort());
                    }
                    registerClient();
                } else {
                    if (platform.isDebugConnections() || platform.isDebugErrors()) {
                        pluginLogger.warn("Failed to connect to host at " + address.getHostString() + ":" + address.getPort() + ": " + (future.cause() != null ? future.cause().getMessage() : "Unknown error"));
                    } else {
                        pluginLogger.warn("Failed to connect to host server. Check your settings.yml configuration.");
                    }
                    shutdownGroupAndNotify();
                }
            } catch (InterruptedException e) {
                if (platform.isDebugErrors()) {
                    pluginLogger.error("Connection interrupted: " + e.getMessage());
                }
                Thread.currentThread().interrupt();
                shutdownGroupAndNotify();
            }
        }).exceptionally(throwable -> {
            if (platform.isDebugErrors()) {
                pluginLogger.error("Error connecting to host: " + (throwable != null ? throwable.getMessage() : "Unknown error"));
            } else {
                pluginLogger.warn("Failed to connect to host: server. Check your settings.yml configuration.");
            }
            shutdownGroupAndNotify();
            return null;
        });
    }

    public void close() {
        if (channel != null) channel.close();
        if (group != null) group.shutdownGracefully();
        if (platform.isDebugConnections()) {
            pluginLogger.info("Netty client connection closed");
        }
    }

    private void registerClient() {
        String secretCode = platform.getSecretCode();
        if (secretCode == null || secretCode.isEmpty()) return;

        ClientRegisterMessage clientRegisterMsg = new ClientRegisterMessage(platform.getServerName(), secretCode);
        String json = gson.toJson(clientRegisterMsg);
        send(json);

        if (platform.isDebugCommandRegistrations()) {
            pluginLogger.info("Sent client registration message.");
        }
    }

    public void registerCommands(List<Command> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        String secret = platform.getSecretCode();
        RegisterMessage<Command> registerMsg = new RegisterMessage.Builder<Command>()
                .type("register")
                .serverName(platform.getServerName())
                .pluginName("DiscordBM")
                .commands(commands)
                .secret(secret)
                .build();
        String json = gson.toJson(registerMsg);
        send(json);

        if (platform.isDebugCommandRegistrations()) {
            pluginLogger.info("Sent register message with commands: " + commands.stream().map(Command::getName).collect(Collectors.joining(", ")));
        }
    }

    public void send(String message) {
        if (isActive()) {
            message = autoDetectAndUpdateResponseType(message);
            channel.writeAndFlush(message);
        } else {
            pluginLogger.warn("Netty channel not active. Message not sent: " + message);
        }
    }

    private String autoDetectAndUpdateResponseType(String message) {
        try {
            JsonObject json = new com.google.gson.JsonParser().parse(message).getAsJsonObject();
            String type = json.get("type") != null ? json.get("type").getAsString() : null;

            if ("response".equals(type)) {
                ResponseMessage respMsg = gson.fromJson(message, ResponseMessage.class);
                ResponseTypeDetector.ResponseType responseType = ResponseTypeDetector.determineResponseType(respMsg);
                ResponseFlags updatedFlags = ResponseTypeDetector.updateFlagsForResponseType(respMsg, responseType);
                ResponseMessage updatedRespMsg = new ResponseMessage.Builder()
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
                
                if (platform.isDebugCommandRegistrations()) {
                    pluginLogger.info("Auto-detected response type: %s for message", responseType);
                }
                
                return gson.toJson(updatedRespMsg);
            }
        } catch (Exception e) {
            if (platform.isDebugErrors()) {
                pluginLogger.error("Error auto-detecting response type: " + e.getMessage());
            }
        }
        
        return message;
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }
}