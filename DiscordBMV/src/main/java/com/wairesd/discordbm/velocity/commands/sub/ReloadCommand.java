package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.config.ConfigManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.scheduler.WebhookScheduler;

public class ReloadCommand {

    private final DiscordBMHPlatformManager platformManager;

    public ReloadCommand(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    public void execute(CommandSource source, MessageContext context) {
        if (!source.hasPermission("discordbotmanager.reload")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }

        WebhookScheduler.shutdown();
        ConfigManager.ConfigureReload();
        WebhookScheduler.start();

        if (platformManager.getNettyServer() != null) {
            platformManager.updateActivity();
            platformManager.getCommandManager().loadAndRegisterCommands();
        }

        source.sendMessage(Messages.getComponent(Messages.Keys.RELOAD_SUCCESS, context));
    }
}
