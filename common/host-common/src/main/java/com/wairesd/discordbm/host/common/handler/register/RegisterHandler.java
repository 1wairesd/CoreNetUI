package com.wairesd.discordbm.host.common.handler.register;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wairesd.discordbm.common.models.register.RegisterMessage;
import com.wairesd.discordbm.host.common.models.command.CommandRegistrationService;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.Database;
import com.wairesd.discordbm.host.common.models.command.CommandDefinition;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.host.common.network.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class RegisterHandler {

    private static final Logger logger = LoggerFactory.getLogger("DiscordBM");
    private static final Gson gson = new Gson();
    private static final String INVALID_SECRET_ERROR = "Error: Invalid secret code";
    private static final String PLUGIN_NAME_METHOD = "pluginName";

    private final NettyServerHandler handler;
    private final Database dbManager;
    private final NettyServer nettyServer;
    private final CommandRegistrationService commandRegisterService;
    private boolean authenticated = false;

    public RegisterHandler(NettyServerHandler handler, Database dbManager, NettyServer nettyServer) {
        this.handler = handler;
        this.dbManager = dbManager;
        this.nettyServer = nettyServer;
        this.commandRegisterService = nettyServer.getCommandRegistrationService();
    }

    public void handleRegister(ChannelHandlerContext ctx, String message, String ip, int port) {
        RegisterMessage<CommandDefinition> registerMessage = parseRegisterMessage(message);

        if (!validateSecretCode(ctx, registerMessage, ip)) {
            return;
        }

        processRegistration(ctx, registerMessage);
    }

    private RegisterMessage<CommandDefinition> parseRegisterMessage(String message) {
        return gson.fromJson(
                message,
                new TypeToken<RegisterMessage<CommandDefinition>>(){}.getType()
        );
    }

    private boolean validateSecretCode(ChannelHandlerContext ctx, RegisterMessage<CommandDefinition> registerMessage, String ip) {
        if (!isSecretCodeValid(registerMessage.secret())) {
            handleInvalidSecret(ctx, ip);
            return false;
        }
        return true;
    }

    private boolean isSecretCodeValid(String secret) {
        return secret != null && secret.equals(Settings.getSecretCode());
    }

    private void handleInvalidSecret(ChannelHandlerContext ctx, String ip) {
        ctx.writeAndFlush(INVALID_SECRET_ERROR);
        dbManager.incrementFailedAttempt(ip);
        ctx.close();
    }

    private void processRegistration(ChannelHandlerContext ctx, RegisterMessage<CommandDefinition> registerMessage) {
        String serverName = registerMessage.serverName();
        String pluginName = registerMessage.pluginName();
        List<CommandDefinition> commands = registerMessage.commands();

        setServerName(ctx, serverName);
        processCommands(ctx, commands, serverName, pluginName);
    }

    private void setServerName(ChannelHandlerContext ctx, String serverName) {
        nettyServer.setServerName(ctx.channel(), serverName);
    }

    private void processCommands(ChannelHandlerContext ctx, List<CommandDefinition> commands, String serverName, String pluginName) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (CommandDefinition cmd : commands) {
            processCommand(ctx, cmd, serverName, pluginName);
        }
    }

    private void processCommand(ChannelHandlerContext ctx, CommandDefinition cmd, String serverName, String pluginName) {
        registerCommand(ctx, cmd, serverName);
        String actualPluginName = determinePluginName(cmd, pluginName);
        registerAddonCommand(cmd, actualPluginName);
        logCommandRegistration(cmd, actualPluginName, serverName);
    }

    private void registerCommand(ChannelHandlerContext ctx, CommandDefinition cmd, String serverName) {
        commandRegisterService.registerCommands(serverName, List.of(cmd), ctx.channel());
    }

    private String determinePluginName(CommandDefinition cmd, String defaultPluginName) {
        String extractedPluginName = extractPluginNameFromCommand(cmd);
        return isValidPluginName(extractedPluginName) ? extractedPluginName : defaultPluginName;
    }

    private String extractPluginNameFromCommand(CommandDefinition cmd) {
        try {
            Method method = cmd.getClass().getMethod(PLUGIN_NAME_METHOD);
            Object value = method.invoke(cmd);

            if (value instanceof String pluginName && !pluginName.isEmpty()) {
                return pluginName;
            }
        } catch (Exception ignored) {
            // Reflection failed, return null to use default
        }
        return null;
    }

    private boolean isValidPluginName(String pluginName) {
        return pluginName != null && !pluginName.isEmpty();
    }

    private void registerAddonCommand(CommandDefinition cmd, String pluginName) {
        if (isValidPluginName(pluginName)) {
            nettyServer.registerAddonCommand(cmd.name(), pluginName);
        }
    }

    private void logCommandRegistration(CommandDefinition cmd, String pluginName, String serverName) {
        if (Settings.isDebugCommandRegistrations()) {
            logger.info("Registered command '{}' from plugin '{}' for server '{}'",
                    cmd.name(), pluginName, serverName);
        }
    }
}