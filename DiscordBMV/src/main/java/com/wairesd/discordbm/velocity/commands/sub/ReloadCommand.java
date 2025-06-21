package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.host.common.config.ConfigManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;

public class ReloadCommand {

    private final DiscordBMV plugin;

    public ReloadCommand(DiscordBMV plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSource source, MessageContext context) {
        if (!source.hasPermission("discordbotmanager.reload")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }

        ConfigManager.ConfigureReload();

        if (plugin.getNettyServer() != null) {
            plugin.updateActivity();
            plugin.getCommandManager().loadAndRegisterCommands();
        }

        source.sendMessage(Messages.getComponent(Messages.Keys.RELOAD_SUCCESS, context));
    }
}
