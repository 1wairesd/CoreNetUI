package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.config.configurators.Messages;
import net.kyori.adventure.text.Component;

import java.util.stream.Collectors;

public class CommandsCommand {

    private final DiscordBMV plugin;

    public CommandsCommand(DiscordBMV plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSource source) {
        if (!source.hasPermission("discordbotmanager.commands")) {
            source.sendMessage(Messages.getParsedMessage("no-permission", null));
            return;
        }

        var commandToServers = plugin.getNettyServer().getCommandToServers();
        if (commandToServers.isEmpty()) {
            source.sendMessage(Messages.getParsedMessage("no-commands-registered", null));
            return;
        }

        for (var entry : commandToServers.entrySet()) {
            String command = entry.getKey();
            String serverList = entry.getValue().stream()
                    .map(server -> server.serverName())
                    .collect(Collectors.joining(", "));
            source.sendMessage(Component.text(command + ": " + serverList));
        }
    }
}
