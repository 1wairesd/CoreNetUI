package com.wairesd.discordbm.host.common.handler.unregister;

import com.wairesd.discordbm.common.models.unregister.UnregisterMessage;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UnregisterHandler {

    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final String INVALID_SECRET_ERROR = "Error: Invalid secret code";

    private final NettyServer nettyServer;

    public UnregisterHandler(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public void handleUnregister(ChannelHandlerContext ctx, UnregisterMessage unregMsg) {
        if (!validateSecretCode(ctx, unregMsg)) {
            return;
        }

        String serverName = unregMsg.serverName;
        String commandName = unregMsg.commandName;

        unregisterCommandFromServer(serverName, commandName);
        logUnregistrationIfDebugEnabled(commandName, serverName);
    }

    private boolean validateSecretCode(ChannelHandlerContext ctx, UnregisterMessage unregMsg) {
        if (!isSecretCodeValid(unregMsg.secret)) {
            sendInvalidSecretError(ctx);
            return false;
        }
        return true;
    }

    private boolean isSecretCodeValid(String secret) {
        return secret != null && secret.equals(Settings.getSecretCode());
    }

    private void sendInvalidSecretError(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(INVALID_SECRET_ERROR);
    }

    private void unregisterCommandFromServer(String serverName, String commandName) {
        List<NettyServer.ServerInfo> servers = getServersForCommand(commandName);

        if (servers != null) {
            removeServerFromCommand(servers, serverName);
            cleanupEmptyCommand(servers, commandName);
        }
    }

    private List<NettyServer.ServerInfo> getServersForCommand(String commandName) {
        return nettyServer.getCommandToServers().get(commandName);
    }

    private void removeServerFromCommand(List<NettyServer.ServerInfo> servers, String serverName) {
        servers.removeIf(serverInfo -> serverInfo.serverName().equals(serverName));
    }

    private void cleanupEmptyCommand(List<NettyServer.ServerInfo> servers, String commandName) {
        if (servers.isEmpty()) {
            nettyServer.getCommandDefinitions().remove(commandName);
        }
    }

    private void logUnregistrationIfDebugEnabled(String commandName, String serverName) {
        if (Settings.isDebugCommandRegistrations()) {
            logger.warn("Unregistered command {} for server {}", commandName, serverName);
        }
    }
}