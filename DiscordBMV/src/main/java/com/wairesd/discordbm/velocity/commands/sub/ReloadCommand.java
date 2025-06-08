package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.config.ConfigManager;
import com.wairesd.discordbm.velocity.config.configurators.Messages;

public class ReloadCommand {

    private final DiscordBMV plugin;

    public ReloadCommand(DiscordBMV plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSource source) {
        if (!source.hasPermission("discordbotmanager.reload")) {
            source.sendMessage(Messages.getParsedMessage("no-permission", null));
            return;
        }

        ConfigManager.ConfigureReload();

        if (plugin.getNettyServer() != null) {
            plugin.updateActivity();
            plugin.getCommandManager().loadAndRegisterCommands();
        }

        source.sendMessage(Messages.getParsedMessage("reload-success", null));
    }
}
