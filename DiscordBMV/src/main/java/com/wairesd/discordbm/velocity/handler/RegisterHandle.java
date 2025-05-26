package com.wairesd.discordbm.velocity.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wairesd.discordbm.common.models.register.RegisterMessage;
import com.wairesd.discordbm.velocity.commands.models.CommandRegistrationService;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.database.DatabaseManager;
import com.wairesd.discordbm.velocity.models.command.CommandDefinition;
import com.wairesd.discordbm.velocity.network.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RegisterHandle {
    private static final Logger logger = LoggerFactory.getLogger("DiscordBMV");
    private final DatabaseManager dbManager;
    private final NettyServer nettyServer;
    private final Gson gson = new Gson();
    private boolean authenticated = false;
    private final CommandRegistrationService commandRegisterService;

    public RegisterHandle(DatabaseManager dbManager, NettyServer nettyServer) {
        this.dbManager = dbManager;
        this.nettyServer = nettyServer;
        this.commandRegisterService = new CommandRegistrationService(nettyServer);
    }

    public void handleRegister(ChannelHandlerContext ctx, RegisterMessage regMsg, String ip, int port) {
        if (regMsg.secret() == null || !regMsg.secret().equals(Settings.getSecretCode())) {
            ctx.writeAndFlush("Error: Invalid secret code");
            dbManager.incrementFailedAttempt(ip);
            ctx.close();
            return;
        }

        if (!authenticated) {
            authenticated = true;
            dbManager.resetAttempts(ip);
            if (Settings.isDebugAuthentication()) {
                logger.info("Client {} IP - {} Port - {} authenticated successfully", regMsg.serverName(), ip, port);
            }
        }

        nettyServer.setServerName(ctx.channel(), regMsg.serverName());
        if (regMsg.commands() != null && !regMsg.commands().isEmpty()) {
            if (Settings.isDebugPluginConnections()) {
                logger.info("Plugin {} registered commands for server {}", regMsg.pluginName(), regMsg.serverName());
            }
            var commandsElement = gson.toJsonTree(regMsg.commands());
            List<CommandDefinition> commandDefinitions = gson.fromJson(commandsElement, new TypeToken<List<CommandDefinition>>() {}.getType());
            commandRegisterService.registerCommands(regMsg.serverName(), commandDefinitions, ctx.channel());
        }
    }
}
