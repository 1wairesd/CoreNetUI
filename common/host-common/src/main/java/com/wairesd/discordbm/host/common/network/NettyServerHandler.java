package com.wairesd.discordbm.host.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.common.models.register.ClientRegisterMessage;
import com.wairesd.discordbm.common.models.unregister.UnregisterMessage;
import com.wairesd.discordbm.common.models.request.AddRoleRequest;
import com.wairesd.discordbm.common.models.request.RemoveRoleRequest;
import com.wairesd.discordbm.common.models.request.WebhookEventRequest;
import com.wairesd.discordbm.common.models.placeholders.response.CanHandleResponse;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import com.wairesd.discordbm.host.common.database.Database;
import com.wairesd.discordbm.host.common.handler.register.ClientRegisterHandler;
import com.wairesd.discordbm.host.common.handler.register.RegisterHandler;
import com.wairesd.discordbm.host.common.discord.response.ResponseHandler;
import com.wairesd.discordbm.host.common.handler.role.AddRoleHandler;
import com.wairesd.discordbm.host.common.handler.role.RemoveRoleHandler;
import com.wairesd.discordbm.host.common.handler.unregister.UnregisterHandler;
import com.wairesd.discordbm.host.common.service.WebhookEventService;
import com.wairesd.discordbm.host.common.utils.WebhookSender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.List;

public class NettyServerHandler extends SimpleChannelInboundHandler<String>
        implements ClientRegisterHandler.NettyServerHandlerContext {

    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final Gson gson = new Gson();
    private static final ExecutorService messageExecutor = createMessageExecutor();

    private final Object jda;
    private final Database dbManager;
    private final NettyServer nettyServer;
    private final RegisterHandler registerHandler;
    private final UnregisterHandler unregisterHandler;
    private final ClientRegisterHandler clientRegisterHandle;
    private boolean authenticated = false;

    public NettyServerHandler(NettyServer nettyServer, Object jda, Database dbManager) {
        this.nettyServer = nettyServer;
        this.jda = jda;
        this.dbManager = dbManager;
        this.registerHandler = new RegisterHandler(this, dbManager, nettyServer);
        this.unregisterHandler = new UnregisterHandler(nettyServer);
        this.clientRegisterHandle = new ClientRegisterHandler(dbManager, nettyServer, this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String ip = getClientIp(ctx);
        nettyServer.setConnectTime(ctx.channel(), System.currentTimeMillis());

        logConnectionIfDebugEnabled(ctx);
        checkAndHandleBlockedIp(ctx, ip);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        messageExecutor.execute(() -> processMessage(ctx, msg));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        handleClientDisconnection(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (isConnectionResetException(cause)) {
            handleConnectionReset(ctx);
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
        shutdownMessageExecutor();
    }

    private static ExecutorService createMessageExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    private String getClientIp(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
    }

    private int getClientPort(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
    }

    private void logConnectionIfDebugEnabled(ChannelHandlerContext ctx) {
        if (Settings.isDebugConnections()) {
            logger.info("Client connected: {}", ctx.channel().remoteAddress());
        }
    }

    private void checkAndHandleBlockedIp(ChannelHandlerContext ctx, String ip) {
        dbManager.isBlocked(ip).thenAcceptAsync(isBlocked -> {
            if (isBlocked) {
                logger.warn("Blocked connection attempt from {}", ip);
                ctx.writeAndFlush("Error: IP blocked due to multiple failed attempts");
                ctx.close();
            }
        }, ctx.executor());
    }

    private void processMessage(ChannelHandlerContext ctx, String msg) {
        String ip = getClientIp(ctx);
        int port = getClientPort(ctx);

        if (!authenticated) {
            handleUnauthenticatedMessage(ctx, msg, ip, port);
            return;
        }

        logClientResponseIfDebugEnabled(msg);
        handleAuthenticatedMessage(ctx, msg, ip, port);
    }

    private void handleUnauthenticatedMessage(ChannelHandlerContext ctx, String msg, String ip, int port) {
        JsonObject json = gson.fromJson(msg, JsonObject.class);
        String type = json.get("type").getAsString();

        if ("client_register".equals(type)) {
            ClientRegisterMessage regMsg = gson.fromJson(json, ClientRegisterMessage.class);
            clientRegisterHandle.handleClientRegister(ctx, regMsg, ip, port);
        } else {
            handleInvalidAuthentication(ctx, ip, port);
        }
    }

    private void handleInvalidAuthentication(ChannelHandlerContext ctx, String ip, int port) {
        if (Settings.isDebugAuthentication()) {
            logger.warn("Client {}:{} was disconnected due to invalid authentication key.", ip, port);
        }
        dbManager.incrementFailedAttempt(ip);
        ctx.close();
    }

    private void logClientResponseIfDebugEnabled(String msg) {
        if (Settings.isDebugClientResponses()) {
            logger.info("Received message from client: {}", msg);
        }
    }

    private void handleAuthenticatedMessage(ChannelHandlerContext ctx, String msg, String ip, int port) {
        JsonObject json = gson.fromJson(msg, JsonObject.class);
        String type = json.get("type").getAsString();

        if (handleRoleRequests(ctx, msg, type)) {
            return;
        }

        switch (type) {
            case "register":
                registerHandler.handleRegister(ctx, msg, ip, port);
                break;
            case "unregister":
                handleUnregisterMessage(ctx, msg);
                break;
            case "form":
                handleFormMessage(msg);
                break;
            case "direct_message":
                handleDirectMessage(msg);
                break;
            case "channel_message":
                handleChannelMessage(msg);
                break;
            case "can_handle_response":
                handleCanHandleResponse(msg);
                break;
            case "placeholders_response":
                handlePlaceholdersResponse(msg);
                break;
            case "edit_message":
                handleEditMessage(msg);
                break;
            case "edit_component":
                handleEditComponent(msg);
                break;
            case "delete_message":
                handleDeleteMessage(json);
                return;
            case "request":
            case "response":
                handleResponseMessage(msg);
                break;
            case "send_webhook":
                handleSendWebhook(ctx, json);
                return;
            case "webhook_event":
                handleWebhookEvent(json);
                return;
            default:
                logger.warn("Unknown message type: {}", type);
        }
    }

    private boolean handleRoleRequests(ChannelHandlerContext ctx, String msg, String type) {
        if ("add_role".equals(type)) {
            AddRoleRequest req = gson.fromJson(msg, AddRoleRequest.class);
            new AddRoleHandler(jda).handle(ctx, req);
            return true;
        } else if ("remove_role".equals(type)) {
            RemoveRoleRequest req = gson.fromJson(msg, RemoveRoleRequest.class);
            new RemoveRoleHandler(jda).handle(ctx, req);
            return true;
        }
        return false;
    }

    private void handleUnregisterMessage(ChannelHandlerContext ctx, String msg) {
        UnregisterMessage unregMsg = gson.fromJson(msg, UnregisterMessage.class);
        unregisterHandler.handleUnregister(ctx, unregMsg);
    }

    private void handleFormMessage(String msg) {
        ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
        ResponseHandler.handleFormOnly(respMsg);
    }

    private void handleDirectMessage(String msg) {
        ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
        ResponseHandler.sendDirectMessage(respMsg);
    }

    private void handleChannelMessage(String msg) {
        ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
        ResponseHandler.sendChannelMessage(respMsg);
    }

    private void handleCanHandleResponse(String msg) {
        CanHandleResponse resp = gson.fromJson(msg, CanHandleResponse.class);
        CompletableFuture<Boolean> future = nettyServer.getCanHandleFutures().remove(resp.requestId());
        if (future != null) {
            future.complete(resp.canHandle());
        }
    }

    private void handlePlaceholdersResponse(String msg) {
        PlaceholdersResponse resp = gson.fromJson(msg, PlaceholdersResponse.class);
        CompletableFuture<PlaceholdersResponse> future = nettyServer.getPlaceholderFutures().remove(resp.requestId());
        if (future != null) {
            future.complete(resp);
        }
    }

    private void handleEditMessage(String msg) {
        ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
        ResponseHandler.editMessage(respMsg);
    }

    private void handleEditComponent(String msg) {
        ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
        ResponseHandler.editComponent(respMsg);
    }

    private void handleDeleteMessage(JsonObject json) {
        String label = json.get("label").getAsString();
        boolean deleteAll = !json.has("delete_all") || json.get("delete_all").getAsBoolean();
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("delete_message")
                .requestId(label)
                .deleteAll(deleteAll)
                .build();
        ResponseHandler.deleteMessage(respMsg);
    }

    private void handleResponseMessage(String msg) {
        ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
        ResponseHandler.handleResponse(respMsg);
    }

    private void handleSendWebhook(ChannelHandlerContext ctx, JsonObject json) {
        String webhookName = json.get("webhookName").getAsString();
        String message = json.get("message").getAsString();

        var webhook = Webhooks.getWebhooks().stream()
                .filter(w -> w.name().equals(webhookName) && w.enabled())
                .findFirst()
                .orElse(null);

        if (webhook == null) {
            ctx.writeAndFlush("{\"type\":\"error\",\"message\":\"Webhook not found: " + webhookName + "\"}");
            return;
        }

        WebhookSender.sendWebhook(webhook.url(), message);
    }

    private void handleWebhookEvent(JsonObject json) {
        try {
            String data = json.get("data").getAsString();
            WebhookEventRequest request = gson.fromJson(data, WebhookEventRequest.class);

            if (request != null) {
                processWebhookEventRequest(request);
            }
        } catch (Exception e) {
            logger.warn("Error handling webhook event from client: {}", e.getMessage());
        }
    }

    private void processWebhookEventRequest(WebhookEventRequest request) {
        switch (request.getType()) {
            case "player_join":
                WebhookEventService.handlePlayerJoinEvent(
                        request.getPlayerName(),
                        request.getPlayerIp(),
                        request.getServerName()
                );
                break;

            case "player_quit":
                WebhookEventService.handlePlayerQuitEvent(
                        request.getPlayerName(),
                        request.getPlayerIp(),
                        request.getReason(),
                        request.getServerName()
                );
                break;
        }
    }

    private void handleClientDisconnection(ChannelHandlerContext ctx) {
        String clientId = nettyServer.getServerName(ctx.channel());
        if (clientId != null) {
            processServerCommandsOnDisconnection(clientId);
        }
        nettyServer.removeServer(ctx.channel());
    }

    private void processServerCommandsOnDisconnection(String clientId) {
        for (var entry : nettyServer.getCommandToPlugin().entrySet()) {
            String command = entry.getKey();
            String pluginName = entry.getValue();
            List<NettyServer.ServerInfo> servers = nettyServer.getServersForCommand(command);
            if (servers != null && servers.stream().anyMatch(s -> clientId.equals(s.serverName()))) {
                // Processing logic for server disconnection - kept empty as in original
            }
        }
    }

    private boolean isConnectionResetException(Throwable cause) {
        return cause instanceof java.net.SocketException && cause.getMessage().equals("Connection reset");
    }

    private void handleConnectionReset(ChannelHandlerContext ctx) {
        String serverName = nettyServer.getServerName(ctx.channel());
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = remoteAddress.getAddress().getHostAddress();
        int port = remoteAddress.getPort();

        if (Settings.isDebugConnections()) {
            logger.warn("Disconnected from server: {}, IP: {}, Port: {}",
                    serverName != null ? serverName : "Unknown", ip, port);
        }
    }

    private static void shutdownMessageExecutor() {
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