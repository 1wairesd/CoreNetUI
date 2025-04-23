package com.wairesd.discordbm.bukkit.api;

import com.google.gson.Gson;
import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import com.wairesd.discordbm.bukkit.handler.DiscordCommandHandler;
import com.wairesd.discordbm.bukkit.models.command.Command;
import com.wairesd.discordbm.bukkit.models.unregister.UnregisterMessage;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.register.RegisterMessage;

import java.util.List;

// Provides an API for other Bukkit plugins to register commands and send responses via Netty.
public class DiscordBotManagerBukkitApi {
    private final DiscordBMB plugin;
    private final Gson gson = new Gson();

    public DiscordBotManagerBukkitApi(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(Command command, DiscordCommandHandler handler, DiscordBMB.DiscordCommandRegistrationListener listener) {
        plugin.registerCommandHandler(command.name, handler, listener, command);
        if (plugin.getNettyClient() != null && plugin.getNettyClient().isActive()) {
            sendRegistrationMessage(command);
        }
    }

    private void sendRegistrationMessage(Command command) {
        String secretCode = Settings.getSecretCode();
        if (secretCode == null || secretCode.isEmpty()) {
            return;
        }

        RegisterMessage<Command> registerMsg = new RegisterMessage<>(
                "register",
                plugin.getServerName(),
                command.pluginName,
                List.of(command),
                secretCode
        );

        String json = gson.toJson(registerMsg);
        plugin.sendNettyMessage(json);
        if (Settings.isDebugCommandRegistrations()) {
            plugin.getLogger().info("Sent registration message for command: " + command.name);
        }
    }

    public void unregisterCommand(String commandName, String pluginName) {
        if (plugin.getNettyClient() != null && plugin.getNettyClient().isActive()) {
            sendUnregistrationMessage(commandName, pluginName);
        }
    }

    private void sendUnregistrationMessage(String commandName, String pluginName) {
        String secretCode = Settings.getSecretCode();
        if (secretCode == null || secretCode.isEmpty()) {
            return;
        }

        UnregisterMessage unregisterMsg = new UnregisterMessage(
                plugin.getServerName(),
                pluginName,
                commandName,
                secretCode
        );
        String json = gson.toJson(unregisterMsg);
        plugin.sendNettyMessage(json);
        if (Settings.isDebugCommandRegistrations()) {
            plugin.getLogger().info("Sent unregistration message for command: " + commandName);
        }
    }

    public void sendResponse(String requestId, EmbedDefinition embed) {
        plugin.sendResponse(requestId, gson.toJson(embed));
    }

    public void sendNettyMessage(String message) {
        plugin.sendNettyMessage(message);
    }
}