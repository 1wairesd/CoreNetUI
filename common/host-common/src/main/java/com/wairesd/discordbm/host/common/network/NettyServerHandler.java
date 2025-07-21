package com.wairesd.discordbm.host.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.common.models.register.ClientRegisterMessage;
import com.wairesd.discordbm.common.models.unregister.UnregisterMessage;
import com.wairesd.discordbm.common.models.request.AddRoleRequest;
import com.wairesd.discordbm.common.models.request.RemoveRoleRequest;
import com.wairesd.discordbm.common.models.placeholders.response.CanHandleResponse;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.config.configurators.CommandEphemeral;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.Database;
import com.wairesd.discordbm.host.common.handler.register.ClientRegisterHandler;
import com.wairesd.discordbm.host.common.handler.register.RegisterHandler;
import com.wairesd.discordbm.host.common.discord.response.ResponseHandler;
import com.wairesd.discordbm.host.common.handler.role.AddRoleHandler;
import com.wairesd.discordbm.host.common.handler.role.RemoveRoleHandler;
import com.wairesd.discordbm.host.common.handler.unregister.UnregisterHandler;
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
    private final Object jda;
    private final Database dbManager;
    private final NettyServer nettyServer;
    private boolean authenticated = false;
    private final RegisterHandler registerHandler;
    private final UnregisterHandler unregisterHandler;
    private final ClientRegisterHandler clientRegisterHandle;
    private static final ExecutorService messageExecutor = 
        new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

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
        String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        nettyServer.setConnectTime(ctx.channel(), System.currentTimeMillis());
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

        if ("add_role".equals(type)) {
            AddRoleRequest req = gson.fromJson(msg, AddRoleRequest.class);
            new AddRoleHandler(jda).handle(ctx, req);
            return;
        } else if ("remove_role".equals(type)) {
            RemoveRoleRequest req = gson.fromJson(msg, RemoveRoleRequest.class);
            new RemoveRoleHandler(jda).handle(ctx, req);
            return;
        }

        if ("register".equals(type)) {
            registerHandler.handleRegister(ctx, msg, ip, port);
        } else if ("unregister".equals(type)) {
            UnregisterMessage unregMsg = gson.fromJson(msg, UnregisterMessage.class);
            unregisterHandler.handleUnregister(ctx, unregMsg);
        } else if ("form".equals(type)) {
            ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
            ResponseHandler.handleFormOnly(respMsg);
        } else if ("direct_message".equals(type)) {
            ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
            ResponseHandler.sendDirectMessage(respMsg);
        } else if ("channel_message".equals(type)) {
            ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
            ResponseHandler.sendChannelMessage(respMsg);
        } else if ("can_handle_response".equals(type)) {
            CanHandleResponse resp = gson.fromJson(msg, CanHandleResponse.class);
            CompletableFuture<Boolean> future = nettyServer.getCanHandleFutures().remove(resp.requestId());
            if (future != null) {
                future.complete(resp.canHandle());
            }
        } else if ("placeholders_response".equals(type)) {
            PlaceholdersResponse resp = gson.fromJson(msg, PlaceholdersResponse.class);
            CompletableFuture<PlaceholdersResponse> future = nettyServer.getPlaceholderFutures().remove(resp.requestId());
            if (future != null) {
                future.complete(resp);
            }
        } else if ("edit_message".equals(type)) {
            ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
            ResponseHandler.editMessage(respMsg);
        } else if ("edit_component".equals(type)) {
            ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
            ResponseHandler.editComponent(respMsg);
        } else if ("delete_message".equals(type)) {
            String label = json.get("label").getAsString();
            boolean deleteAll = !json.has("delete_all") || json.get("delete_all").getAsBoolean();
            ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("delete_message")
                .requestId(label)
                .deleteAll(deleteAll)
                .build();
            ResponseHandler.deleteMessage(respMsg);
            return;
        } else if ("request".equals(type) || "response".equals(type)) {
            ResponseMessage respMsg = gson.fromJson(msg, ResponseMessage.class);
            ResponseHandler.handleResponse(respMsg);
        } else if ("ephemeral_rules".equals(type)) {
            String clientId = nettyServer.getServerName(ctx.channel());
            if (json.has("rules") && json.get("rules").isJsonObject()) {
                for (var entry : json.getAsJsonObject("rules").entrySet()) {
                    CommandEphemeral.addOrUpdateClientRule(clientId, entry.getKey(), entry.getValue().getAsBoolean());
                }
            }
            return;
        } else if ("send_webhook".equals(type)) {
            String webhookName = json.get("webhookName").getAsString();
            String message = json.get("message").getAsString();
            var webhook = com.wairesd.discordbm.host.common.config.configurators.Webhooks.getWebhooks().stream()
                .filter(w -> w.name().equals(webhookName) && w.enabled())
                .findFirst()
                .orElse(null);
            if (webhook == null) {
                ctx.writeAndFlush("{\"type\":\"error\",\"message\":\"Webhook not found: " + webhookName + "\"}");
                return;
            }
            com.wairesd.discordbm.host.common.utils.WebhookSender.sendWebhook(webhook.url(), message);
            // ctx.writeAndFlush("{\"type\":\"success\",\"message\":\"Webhook sent: " + webhookName + "\"}"); // Убрано чтобы не было NPE
            return;
        } else {
            logger.warn("Unknown message type: {}", type);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String clientId = nettyServer.getServerName(ctx.channel());
        CommandEphemeral.removeAllClientRules(clientId);
        if (clientId != null) {
            for (var entry : nettyServer.getCommandToPlugin().entrySet()) {
                String command = entry.getKey();
                String pluginName = entry.getValue();
                List<NettyServer.ServerInfo> servers = nettyServer.getServersForCommand(command);
                if (servers != null && servers.stream().anyMatch(s -> clientId.equals(s.serverName()))) {
                    CommandEphemeral.removeAllPluginRules(pluginName);
                }
            }
        }
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