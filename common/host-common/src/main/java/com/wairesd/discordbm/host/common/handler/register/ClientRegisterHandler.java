package com.wairesd.discordbm.host.common.handler.register;

import com.wairesd.discordbm.common.models.register.ClientRegisterMessage;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.Database;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;

public class ClientRegisterHandler {
    private static final String AUTH_SUCCESS_RESPONSE = "{\"type\":\"auth_ok\"}";
    private static final String INVALID_SECRET_ERROR = "Error: Invalid secret code";

    private final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private final Database dbManager;
    private final NettyServer nettyServer;
    private final NettyServerHandlerContext nettyContext;

    public ClientRegisterHandler(Database dbManager, NettyServer nettyServer,
                                 NettyServerHandlerContext nettyContext) {
        this.dbManager = dbManager;
        this.nettyServer = nettyServer;
        this.nettyContext = nettyContext;
    }

    public void handleClientRegister(ChannelHandlerContext ctx, ClientRegisterMessage regMsg,
                                     String ip, int port) {
        if (!isValidSecret(regMsg)) {
            handleInvalidSecret(ctx, ip);
            return;
        }

        if (shouldAuthenticate()) {
            authenticateClient(ctx, regMsg, ip, port);
        }
    }

    private boolean isValidSecret(ClientRegisterMessage regMsg) {
        return regMsg.getSecret() != null && regMsg.getSecret().equals(Settings.getSecretCode());
    }

    private void handleInvalidSecret(ChannelHandlerContext ctx, String ip) {
        ctx.writeAndFlush(INVALID_SECRET_ERROR);
        dbManager.incrementFailedAttempt(ip);
        ctx.close();
    }

    private boolean shouldAuthenticate() {
        return !nettyContext.isAuthenticated();
    }

    private void authenticateClient(ChannelHandlerContext ctx, ClientRegisterMessage regMsg,
                                    String ip, int port) {
        nettyContext.setAuthenticated(true);
        dbManager.resetAttempts(ip);
        nettyServer.setServerName(ctx.channel(), regMsg.getServerName());
        ctx.writeAndFlush(AUTH_SUCCESS_RESPONSE);

        logSuccessfulAuthentication(regMsg, ip, port);
    }

    private void logSuccessfulAuthentication(ClientRegisterMessage regMsg, String ip, int port) {
        if (Settings.isDebugAuthentication()) {
            logger.info("Client {} IP - {} Port - {} authenticated successfully",
                    regMsg.getServerName(), ip, port);
        }
    }

    public interface NettyServerHandlerContext {
        boolean isAuthenticated();
        void setAuthenticated(boolean value);
    }
}