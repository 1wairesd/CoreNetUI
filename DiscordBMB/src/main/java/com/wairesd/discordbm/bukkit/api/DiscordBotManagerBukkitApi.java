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

/**
 * The DiscordBotManagerBukkitApi class provides an API for managing the integration
 * between a Bukkit-based Minecraft server and Discord. This includes functionality
 * for registering and unregistering commands, sending responses, and communicating
 * with Discord using Netty.
 *
 * This API acts as a layer on top of the DiscordBMB plugin, leveraging its features
 * and services for communication and command handling.
 */
public class DiscordBotManagerBukkitApi {
    private final DiscordBMB plugin;
    private final Gson gson = new Gson();

    public DiscordBotManagerBukkitApi(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(Command command,
                                DiscordCommandHandler handler,
                                DiscordBMB.DiscordCommandRegistrationListener listener) {
        plugin.registerCommandHandler(
                command.name, handler, listener, command
        );
        if (plugin.getNettyService().getNettyClient() != null
                && plugin.getNettyService().getNettyClient().isActive()) {
            sendRegistrationMessage(command);
        }
    }

    private void sendRegistrationMessage(Command command) {
        String secret = Settings.getSecretCode();
        if (secret == null || secret.isEmpty()) return;

        RegisterMessage<Command> msg = new RegisterMessage<>(
                "register",
                plugin.getServerName(),
                command.pluginName,
                List.of(command),
                secret
        );
        plugin.getNettyService().sendNettyMessage(gson.toJson(msg));
        if (Settings.isDebugCommandRegistrations()) {
            plugin.getLogger().info("Sent registration message for command: " + command.name);
        }
    }

    public void unregisterCommand(String commandName, String pluginName) {
        if (plugin.getNettyService().getNettyClient() != null
                && plugin.getNettyService().getNettyClient().isActive()) {
            sendUnregistrationMessage(commandName, pluginName);
        }
    }

    private void sendUnregistrationMessage(String commandName, String pluginName) {
        String secret = Settings.getSecretCode();
        if (secret == null || secret.isEmpty()) return;

        UnregisterMessage msg = new UnregisterMessage(
                plugin.getServerName(),
                pluginName,
                commandName,
                secret
        );
        plugin.getNettyService().sendNettyMessage(gson.toJson(msg));
        if (Settings.isDebugCommandRegistrations()) {
            plugin.getLogger().info("Sent unregistration message for command: " + commandName);
        }
    }

    public void sendResponse(String requestId, EmbedDefinition embed) {
        String embedJson = gson.toJson(embed);
        plugin.getNettyService().sendResponse(requestId, embedJson);
    }

    public void sendNettyMessage(String message) {
        plugin.getNettyService().sendNettyMessage(message);
    }
}
