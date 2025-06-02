package com.wairesd.discordbm.api.network;

import com.google.gson.Gson;
import com.wairesd.discordbm.api.platform.Platform;
import com.wairesd.discordbm.api.handle.MessageHandler;
import com.wairesd.discordbm.api.models.command.Command;
import com.wairesd.discordbm.common.models.register.ClientRegisterMessage;
import com.wairesd.discordbm.common.models.register.RegisterMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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
    private boolean closing = false;

    public NettyClient(InetSocketAddress address, Platform platform, PluginLogger pluginLogger) {
        this.address = address;
        this.platform = platform;
        this.pluginLogger = pluginLogger;
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
                            ch.pipeline().addLast("stringDecoder", new StringDecoder(StandardCharsets.UTF_8));
                            ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            ch.pipeline().addLast("stringEncoder", new StringEncoder(StandardCharsets.UTF_8));
                            ch.pipeline().addLast("handler", new MessageHandler(platform, pluginLogger));
                        }
                    });

            try {
                ChannelFuture future = bootstrap.connect(address).sync();
                if (future.isSuccess()) {
                    channel = future.channel();
                    if (platform.isDebugConnections()) {
                        pluginLogger.info("Connected to Velocity at {}:{}", address.getHostString(), address.getPort());
                    }
                    registerClient();
                    platform.onNettyConnected();
                } else {
                    if (platform.isDebugConnections()) {
                        pluginLogger.warn("Failed to connect to Velocity at {}:{}: {}", address.getHostString(), address.getPort(), future.cause().getMessage());
                    }
                    scheduleReconnect();
                }
            } catch (InterruptedException e) {
                if (platform.isDebugErrors()) {
                    pluginLogger.error("Connection interrupted", e);
                }
                Thread.currentThread().interrupt();
            }
        }).exceptionally(throwable -> {
            if (platform.isDebugErrors()) {
                pluginLogger.error("Error connecting to Velocity: {}", throwable.getMessage());
            }
            scheduleReconnect();
            return null;
        });
    }

    public void close() {
        closing = true;
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
                .pluginName("DiscordBMB")
                .commands(commands)
                .secret(secret)
                .build();
        String json = gson.toJson(registerMsg);
        send(json);

        if (platform.isDebugCommandRegistrations()) {
            pluginLogger.info("Sent register message with commands: " + commands.stream().map(Command::getName).collect(Collectors.joining(", ")));
        }
    }

    private void scheduleReconnect() {
        if (!closing) {
            platform.runTaskAsynchronously(() -> connect());
        }
    }

    public void send(String message) {
        if (isActive()) {
            channel.writeAndFlush(message);
        }
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }
}