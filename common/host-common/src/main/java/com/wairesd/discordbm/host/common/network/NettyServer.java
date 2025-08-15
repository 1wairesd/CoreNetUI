package com.wairesd.discordbm.host.common.network;

import com.wairesd.discordbm.common.network.codec.ByteBufDecoder;
import com.wairesd.discordbm.common.network.codec.ByteBufEncoder;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.models.command.CommandRegistrationService;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.Database;
import com.wairesd.discordbm.host.common.models.command.CommandDefinition;
import com.wairesd.discordbm.host.common.utils.ClientInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import net.dv8tion.jda.api.JDA;
import org.slf4j.LoggerFactory;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServer {

    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final int FRAME_MAX_LENGTH = 65535;
    private static final int LENGTH_FIELD_LENGTH = 2;
    private static final int BACKLOG_SIZE = 128;
    private static final int BUFFER_SIZE = 128 * 1024;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private JDA jda;

    private final Map<String, CommandDefinition> commandDefinitions = new HashMap<>();
    private final Map<String, List<ServerInfo>> commandToServers = new HashMap<>();
    private final Map<Channel, String> channelToServerName = new ConcurrentHashMap<>();
    private final Map<String, String> commandToPlugin = new ConcurrentHashMap<>();
    private final Map<Channel, Long> channelConnectTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> canHandleFutures = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<PlaceholdersResponse>> placeholderFutures = new ConcurrentHashMap<>();

    private final int port;
    private final String ip;
    private final Database dbManager;
    private final CommandRegistrationService commandRegistrationService;

    public NettyServer(Database dbManager) {
        this.dbManager = dbManager;
        this.port = Settings.getNettyPort();
        this.ip = Settings.getNettyIp();
        this.commandRegistrationService = new CommandRegistrationService(null, this);
    }

    public void start() {
        initializeEventLoopGroups();

        try {
            ServerBootstrap bootstrap = createServerBootstrap();
            ChannelFuture future = bindServer(bootstrap);
            serverChannel = future.channel();

            logServerStartIfDebugEnabled();
            serverChannel.closeFuture().sync();

        } catch (InterruptedException e) {
            handleInterruptedException(e);
        } catch (Exception e) {
            handleServerStartException(e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        shutdownEventLoopGroups();
        closeServerChannel();
        shutdownNettyServerHandler();
        logShutdownIfDebugEnabled();
    }

    public void sendMessage(Channel channel, String message) {
        if (isChannelActiveAndValid(channel)) {
            channel.writeAndFlush(message);
        }
    }

    public void removeServer(Channel channel) {
        List<String> commandsToRemove = removeChannelFromCommands(channel);
        cleanupEmptyCommands(commandsToRemove);
        removeChannelFromMaps(channel);
    }

    public Channel getChannelByServerName(String serverName) {
        return channelToServerName.entrySet().stream()
                .filter(entry -> entry.getValue().equals(serverName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public void setJda(JDA jda) {
        this.jda = jda;
        this.commandRegistrationService.setJda(jda);
    }

    public void registerAddonCommand(String commandName, String pluginName) {
        commandToPlugin.put(commandName, pluginName);
    }

    public String getPluginForCommand(String commandName) {
        return commandToPlugin.getOrDefault(commandName, "Unknown");
    }

    public void setConnectTime(Channel channel, long time) {
        channelConnectTime.put(channel, time);
    }

    public void setServerName(Channel channel, String serverName) {
        channelToServerName.put(channel, serverName);
    }

    public String getServerName(Channel channel) {
        return channelToServerName.get(channel);
    }

    public List<ServerInfo> getServersForCommand(String command) {
        return commandToServers.getOrDefault(command, new ArrayList<>());
    }

    public List<ClientInfo> getActiveClientsInfo() {
        return ClientInfo.getActiveClientsInfo(channelToServerName, channelConnectTime);
    }

    // Getters
    public Map<Channel, String> getChannelToServerName() {
        return channelToServerName;
    }

    public ConcurrentHashMap<String, CompletableFuture<Boolean>> getCanHandleFutures() {
        return canHandleFutures;
    }

    public ConcurrentHashMap<String, CompletableFuture<PlaceholdersResponse>> getPlaceholderFutures() {
        return placeholderFutures;
    }

    public Map<String, List<ServerInfo>> getCommandToServers() {
        return commandToServers;
    }

    public Map<String, CommandDefinition> getCommandDefinitions() {
        return commandDefinitions;
    }

    public CommandRegistrationService getCommandRegistrationService() {
        return commandRegistrationService;
    }

    public JDA getJda() {
        return jda;
    }

    public Map<String, String> getCommandToPlugin() {
        return commandToPlugin;
    }

    // Private helper methods for start()
    private void initializeEventLoopGroups() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(2 * Runtime.getRuntime().availableProcessors());
    }

    private ServerBootstrap createServerBootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        return bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, BACKLOG_SIZE)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
                .childOption(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
                .childOption(ChannelOption.TCP_NODELAY, true);
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
        ch.pipeline().addLast("frameDecoder",
                new LengthFieldBasedFrameDecoder(FRAME_MAX_LENGTH, 0, LENGTH_FIELD_LENGTH, 0, LENGTH_FIELD_LENGTH));
        ch.pipeline().addLast("byteBufDecoder", new ByteBufDecoder());
        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(LENGTH_FIELD_LENGTH));
        ch.pipeline().addLast("byteBufEncoder", new ByteBufEncoder());
        ch.pipeline().addLast("handler", new NettyServerHandler(this, jda, dbManager));
    }

    private ChannelFuture bindServer(ServerBootstrap bootstrap) throws InterruptedException {
        return (ip == null || ip.isEmpty())
                ? bootstrap.bind(port).sync()
                : bootstrap.bind(ip, port).sync();
    }

    private void logServerStartIfDebugEnabled() {
        if (Settings.isDebugNettyStart()) {
            String bindAddress = (ip == null || ip.isEmpty()) ? "0.0.0.0" : ip;
            logger.info("Netty server started on {}:{}", bindAddress, port);
        }
    }

    private void handleInterruptedException(InterruptedException e) {
        if (Settings.isDebugErrors()) {
            logger.error("Netty server interrupted", e);
        }
        Thread.currentThread().interrupt();
    }

    private void handleServerStartException(Exception e) {
        Throwable cause = (e instanceof BindException) ? e : e.getCause();

        if (cause instanceof BindException) {
            handleBindException();
        } else {
            handleGenericException(e);
        }
    }

    private void handleBindException() {
        logger.error("Port {} is already in use! Please change netty.port in settings.yml or stop the other application.", port);
    }

    private void handleGenericException(Exception e) {
        if (Settings.isDebugErrors()) {
            logger.error("Error starting Netty server: {}", e.getMessage(), e);
        } else {
            logger.error("Error starting Netty server: {}", e.getMessage());
        }
    }

    // Private helper methods for shutdown()
    private void shutdownEventLoopGroups() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    private void closeServerChannel() {
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
        }
    }

    private void shutdownNettyServerHandler() {
        NettyServerHandler.shutdown();
    }

    private void logShutdownIfDebugEnabled() {
        if (Settings.isDebugConnections()) {
            logger.info("Netty server shutdown complete");
        }
    }

    // Private helper methods for sendMessage()
    private boolean isChannelActiveAndValid(Channel channel) {
        return channel != null && channel.isActive();
    }

    // Private helper methods for removeServer()
    private List<String> removeChannelFromCommands(Channel channel) {
        List<String> commandsToRemove = new ArrayList<>();

        for (var entry : commandToServers.entrySet()) {
            entry.getValue().removeIf(serverInfo -> serverInfo.channel() == channel);
            if (entry.getValue().isEmpty()) {
                commandsToRemove.add(entry.getKey());
            }
        }

        return commandsToRemove;
    }

    private void cleanupEmptyCommands(List<String> commandsToRemove) {
        for (String cmd : commandsToRemove) {
            commandToServers.remove(cmd);
            commandDefinitions.remove(cmd);
            commandToPlugin.remove(cmd);
            logCommandRemovalIfDebugEnabled(cmd);
        }
    }

    private void logCommandRemovalIfDebugEnabled(String cmd) {
        if (Settings.isDebugCommandRegistrations()) {
            logger.info("Removed command {} as no servers remain", cmd);
        }
    }

    private void removeChannelFromMaps(Channel channel) {
        channelToServerName.remove(channel);
        channelConnectTime.remove(channel);
    }

    // Record definition
    public record ServerInfo(String serverName, Channel channel) {
    }
}