package com.wairesd.discordbm.host.common.handler;

import com.wairesd.discordbm.common.models.register.ClientRegisterMessage;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.DatabaseManager;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;

public class ClientRegisterHandler {
    private final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final DatabaseManager dbManager;
    private final NettyServer nettyServer;
    private final NettyServerHandlerContext nettyContext;

    public ClientRegisterHandler(DatabaseManager dbManager, NettyServer nettyServer, NettyServerHandlerContext nettyContext) {
        this.dbManager = dbManager;
        this.nettyServer = nettyServer;
        this.nettyContext = nettyContext;
    }

    public void handleClientRegister(ChannelHandlerContext ctx, ClientRegisterMessage regMsg, String ip, int port) {
        if (regMsg.getSecret() == null || !regMsg.getSecret().equals(Settings.getSecretCode())) {
            ctx.writeAndFlush("Error: Invalid secret code");
            dbManager.incrementFailedAttempt(ip);
            ctx.close();
            return;
        }

        if (!nettyContext.isAuthenticated()) {
            nettyContext.setAuthenticated(true);
            dbManager.resetAttempts(ip);
            nettyServer.setServerName(ctx.channel(), regMsg.getServerName());
            if (Settings.isDebugAuthentication()) {
                logger.info("Client {} IP - {} Port - {} authenticated successfully", regMsg.getServerName(), ip, port);
            }
        }
    }

    public interface NettyServerHandlerContext {
        boolean isAuthenticated();
        void setAuthenticated(boolean value);
    }
}
