package com.wairesd.discordbm.host.common.service;

import com.wairesd.discordbm.host.common.config.ConfigManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.manager.WebhookManager;
import com.wairesd.discordbm.host.common.scheduler.WebhookScheduler;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.host.common.utils.ClientInfo;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.common.utils.color.MessageContext;

import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

public class HostCommandService {
    public static String reload(Path dataDirectory, DiscordBMHPlatformManager platformManager) {
        WebhookScheduler.shutdown();
        ConfigManager.ConfigureReload();
        WebhookScheduler.start();
        if (platformManager != null && platformManager.getNettyServer() != null) {
            platformManager.updateActivity();
            platformManager.getCommandManager().loadAndRegisterCommands();
        }
        return Messages.get(Messages.Keys.RELOAD_SUCCESS);
    }

    public static String toggleWebhook(Path dataDirectory, String webhookName, boolean enable) {
        WebhookScheduler.shutdown();
        String result = WebhookManager.handleWebhookToggle(dataDirectory, webhookName, enable);
        WebhookScheduler.start();
        return result;
    }

    public static String listClients(DiscordBMHPlatformManager platformManager) {
        NettyServer nettyServer = platformManager.getNettyServer();
        if (nettyServer == null) {
            return Messages.get(Messages.Keys.NO_ACTIVE_CLIENTS);
        }
        List<ClientInfo> clients = nettyServer.getActiveClientsInfo();
        if (clients.isEmpty()) {
            return Messages.get(Messages.Keys.NO_CONNECTED_CLIENTS);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Clients (").append(clients.size()).append("):");
        for (ClientInfo client : clients) {
            sb.append("\n- ").append(client.name)
              .append(" (").append(client.ip).append(":").append(client.port).append(") time: ")
              .append(formatUptime(client.uptimeMillis));
        }
        return sb.toString();
    }

    private static String formatUptime(long millis) {
        long days = millis / (1000 * 60 * 60 * 24);
        long hours = (millis / (1000 * 60 * 60)) % 24;
        long minutes = (millis / (1000 * 60)) % 60;
        long seconds = (millis / 1000) % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");
        return sb.toString();
    }

    public static String getHelp(MessageContext context) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(Messages.get(Messages.Keys.HELP_HEADER, context));
        joiner.add(Messages.get(Messages.Keys.HELP_RELOAD, context));
        joiner.add(Messages.get(Messages.Keys.HELP_WEBHOOK, context));
        joiner.add(Messages.get(Messages.Keys.HELP_CUSTOM_COMMANDS, context));
        joiner.add(Messages.get(Messages.Keys.HELP_ADDONS_COMMANDS, context));
        return joiner.toString();
    }

    public static String getCustomCommands(MessageContext context) {
        List<CommandStructured> customCommands = com.wairesd.discordbm.host.common.config.configurators.Commands.getCustomCommands();
        if (customCommands.isEmpty()) {
            return Messages.get(Messages.Keys.CUSTOM_COMMANDS_EMPTY, context);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(Messages.Keys.CUSTOM_COMMANDS_HEADER, context, customCommands.size()));
        for (var cmd : customCommands) {
            sb.append("\n").append(Messages.get(Messages.Keys.CUSTOM_COMMANDS_ENTRY, context, cmd.getName()));
        }
        return sb.toString();
    }

    public static String getAddonCommands(DiscordBMHPlatformManager platformManager, MessageContext context) {
        if (platformManager == null || platformManager.getNettyServer() == null) {
            return Messages.get(Messages.Keys.ADDONS_COMMANDS_EMPTY, context);
        }
        var nettyServer = platformManager.getNettyServer();
        var commandToServers = nettyServer.getCommandToServers();
        var customCommands = com.wairesd.discordbm.host.common.config.configurators.Commands.getCustomCommands();
        java.util.Set<String> customNames = new java.util.HashSet<>();
        for (var cmd : customCommands) customNames.add(cmd.getName());
        java.util.Map<String, List<String>> addonCommands = new java.util.HashMap<>();
        for (var entry : commandToServers.entrySet()) {
            if (!customNames.contains(entry.getKey())) {
                String plugin = nettyServer.getPluginForCommand(entry.getKey());
                addonCommands.computeIfAbsent(plugin, k -> new java.util.ArrayList<>()).add(entry.getKey());
            }
        }
        if (addonCommands.isEmpty()) {
            return Messages.get(Messages.Keys.ADDONS_COMMANDS_EMPTY, context);
        }
        int total = addonCommands.values().stream().mapToInt(List::size).sum();
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(Messages.Keys.ADDONS_COMMANDS_HEADER, context, total));
        addonCommands.forEach((plugin, commands) -> {
            sb.append("\n").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_PLUGIN, context, plugin, commands.size()));
            for (String cmd : commands) {
                sb.append("\n").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_ENTRY, context, cmd));
            }
        });
        return sb.toString();
    }
} 