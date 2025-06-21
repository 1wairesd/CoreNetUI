package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.common.utils.color.MessageContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CommandsCommand {
    private final DiscordBMHPlatformManager platformManager;

    public CommandsCommand(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    public void execute(CommandSource source, String[] args, MessageContext context) {
        if (!source.hasPermission("discordbotmanager.commands")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }

        if (args.length < 2) {
            showHelp(source, context);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "custom" -> showCustomCommands(source, context);
            case "addons" -> showAddonCommands(source, context);
            default -> showHelp(source, context);
        }
    }

    private void showHelp(CommandSource source, MessageContext context) {
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_HEADER, context));
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_CUSTOM_COMMANDS, context));
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_ADDONS_COMMANDS, context));
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_RELOAD, context));
    }

    private void showCustomCommands(CommandSource source, MessageContext context) {
        List<CommandStructured> customCommands = Commands.getCustomCommands();
        if (customCommands.isEmpty()) {
            source.sendMessage(Messages.getComponent(Messages.Keys.CUSTOM_COMMANDS_EMPTY, context));
            return;
        }
        source.sendMessage(Messages.getComponent(Messages.Keys.CUSTOM_COMMANDS_HEADER, context, customCommands.size()));
        for (CommandStructured cmd : customCommands) {
            source.sendMessage(Messages.getComponent(Messages.Keys.CUSTOM_COMMANDS_ENTRY, context, cmd.getName()));
        }
    }

    private void showAddonCommands(CommandSource source, MessageContext context) {
        Map<String, List<String>> addonCommands = getAddonCommands();
        if (addonCommands.isEmpty()) {
            source.sendMessage(Messages.getComponent(Messages.Keys.ADDONS_COMMANDS_EMPTY, context));
            return;
        }
        int total = addonCommands.values().stream().mapToInt(List::size).sum();
        source.sendMessage(Messages.getComponent(Messages.Keys.ADDONS_COMMANDS_HEADER, context, total));
        addonCommands.forEach((plugin, commands) -> {
            source.sendMessage(Messages.getComponent(Messages.Keys.ADDONS_COMMANDS_PLUGIN, context, plugin, commands.size()));
            for (String cmd : commands) {
                source.sendMessage(Messages.getComponent(Messages.Keys.ADDONS_COMMANDS_ENTRY, context, cmd));
            }
        });
    }

    private Map<String, List<String>> getAddonCommands() {
        return platformManager.getNettyServer().getCommandToServers().entrySet().stream()
                .filter(entry -> !isCustomCommand(entry.getKey()))
                .collect(Collectors.groupingBy(
                    entry -> platformManager.getNettyServer().getPluginForCommand(entry.getKey()),
                    Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                ));
    }

    private boolean isCustomCommand(String commandName) {
        return Commands.getCustomCommands().stream()
                .anyMatch(cmd -> cmd.getName().equals(commandName));
    }
}
