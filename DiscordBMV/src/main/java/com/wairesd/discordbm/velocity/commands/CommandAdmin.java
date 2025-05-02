package com.wairesd.discordbm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.wairesd.discordbm.common.utils.ColorUtils;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.config.ConfigManager;
import com.wairesd.discordbm.velocity.config.configurators.Messages;

import java.util.stream.Collectors;

public class CommandAdmin implements SimpleCommand {
    private final DiscordBMV plugin;

    public CommandAdmin(DiscordBMV plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(ColorUtils.parseComponent(Messages.getMessage("usage-admin-command")));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!source.hasPermission("discordbotmanager.reload")) {
                    source.sendMessage(ColorUtils.parseComponent(Messages.getMessage("no-permission")));
                    return;
                }
                ConfigManager.ConfigureReload();
                plugin.updateActivity();
                plugin.getCommandManager().loadAndRegisterCommands();
                source.sendMessage(ColorUtils.parseComponent(Messages.getMessage("reload-success")));
                break;
            case "commands":
                if (!source.hasPermission("discordbotmanager.commands")) {
                    source.sendMessage(ColorUtils.parseComponent(Messages.getMessage("no-permission")));
                    return;
                }
                var commandToServers = plugin.getNettyServer().getCommandToServers();
                if (commandToServers.isEmpty()) {
                    source.sendMessage(ColorUtils.parseComponent("No registered commands."));
                    return;
                }
                for (var entry : commandToServers.entrySet()) {
                    String command = entry.getKey();
                    String serverList = entry.getValue().stream()
                            .map(server -> server.serverName())
                            .collect(Collectors.joining(", "));
                    source.sendMessage(ColorUtils.parseComponent("&e" + command + ": &f" + serverList));
                }
                break;

            default:
                source.sendMessage(ColorUtils.parseComponent(Messages.getMessage("usage-admin-command")));
        }
    }
}
