package com.wairesd.discordbm.velocity.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.common.models.register.ClientRegisterMessage;
import com.wairesd.discordbm.common.models.unregister.UnregisterMessage;
import com.wairesd.discordbm.common.models.placeholders.response.CanHandleResponse;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;
import com.wairesd.discordbm.common.models.register.RegisterMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.database.DatabaseManager;
import com.wairesd.discordbm.velocity.handler.ClientRegisterHandler;
import com.wairesd.discordbm.velocity.handler.RegisterHandler;
import com.wairesd.discordbm.velocity.handler.ResponseHandler;
import com.wairesd.discordbm.velocity.handler.UnregisterHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class NettyServerHandler extends SimpleChannelInboundHandler<String>
        implements ClientRegisterHandler.NettyServerHandlerContext {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private static final Gson gson = new Gson();
    private final Object jda;
    private final DatabaseManager dbManager;
    private final NettyServer nettyServer;
    private boolean authenticated = false;
    private final RegisterHandler registerHandler;
    private final UnregisterHandler unregisterHandler;
    private final ResponseHandler responseHandle;
    private final ClientRegisterHandler clientRegisterHandle;
    private static final ExecutorService messageExecutor = 
        new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

    public NettyServerHandler(NettyServer nettyServer, Object jda, DatabaseManager dbManager) {
        this.nettyServer = nettyServer;
        this.jda = jda;
        this.dbManager = dbManager;
        this.registerHandler = new RegisterHandler(this, dbManager, nettyServer);
        this.unregisterHandler = new UnregisterHandler(nettyServer);
        this.responseHandle = new ResponseHandler();
        this.clientRegisterHandle = new ClientRegisterHandler(dbManager, nettyServer, this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        if (Settings.isDebugConnections()) {
            logger.info("Client connected: {}", ctx.channel().remoteAddress());
        }
        dbManager.isBlocked(ip).thenAcceptAsync(isBlocked -> {
            if (isBlocked) {
                if (Settings.isViewConnectedBannedIp()) {
                    logger.warn("Blocked connection attempt from {}", ip);
                }
                ctx.writeAndFlush("Error: IP blocked due to multiple failed attempts");
                ctx.close();
            }
        }, ctx.executor());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        messageExecutor.execute(() -> processMessage(ctx, msg));
    }

    private void processMessage(ChannelHandlerContext ctx, String msg) {
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

        if (!authenticated) {
            JsonObject json = gson.fromJson(msg, JsonObject.class);
            String type = json.get("type").getAsString();

            if ("client_register".equals(type)) {
                ClientRegisterMessage regMsg = gson.fromJson(json, ClientRegisterMessage.class);
                clientRegisterHandle.handleClientRegister(ctx, regMsg, ip, port);
            } else {
                if (Settings.isDebugAuthentication()) {
                    logger.warn("Client {}:{} was disconnected due to invalid authentication key.", ip, port);
                }
                dbManager.incrementFailedAttempt(ip);
                ctx.close();
            }
            return;
        }

        if (Settings.isDebugClientResponses()) {
            logger.info("Received message from client: {}", msg);
        }

        JsonObject json = gson.fromJson(msg, JsonObject.class);
        String type = json.get("type").getAsString();

        if ("register".equals(type)) {
            RegisterMessage regMsg = gson.fromJson(json, RegisterMessage.class);
            registerHandler.handleRegister(ctx, regMsg, ip, port);
        } else if ("unregister".equals(type)) {
            UnregisterMessage unregMsg = gson.fromJson(json, UnregisterMessage.class);
            unregisterHandler.handleUnregister(ctx, unregMsg);
        } else if ("response".equals(type)) {
            responseHandle.handleResponse(json);
        } else if ("can_handle_response".equals(type)) {
            CanHandleResponse resp = gson.fromJson(json, CanHandleResponse.class);
            CompletableFuture<Boolean> future = nettyServer.getCanHandleFutures().remove(resp.requestId());
            if (future != null) {
                future.complete(resp.canHandle());
            }
        } else if ("placeholders_response".equals(type)) {
            PlaceholdersResponse resp = gson.fromJson(json, PlaceholdersResponse.class);
            CompletableFuture<PlaceholdersResponse> future = nettyServer.getPlaceholderFutures().remove(resp.requestId());
            if (future != null) {
                future.complete(resp);
            }
        } else {
            logger.warn("Unknown message type: {}", type);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        nettyServer.removeServer(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof java.net.SocketException && cause.getMessage().equals("Connection reset")) {
            String serverName = nettyServer.getServerName(ctx.channel());
            InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = remoteAddress.getAddress().getHostAddress();
            int port = remoteAddress.getPort();
            if (Settings.isDebugConnections()) {
                logger.warn("Disconnected from server: {}, IP: {}, Port: {}",
                        serverName != null ? serverName : "Unknown", ip, port);
            }
        } else if (Settings.isDebugErrors()) {
            logger.error("Exception in Netty channel: {}", ctx.channel().remoteAddress(), cause);
            ctx.close();
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean value) {
        this.authenticated = value;
    }

    public static void shutdown() {
        messageExecutor.shutdown();
        try {
            if (!messageExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                messageExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            messageExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}