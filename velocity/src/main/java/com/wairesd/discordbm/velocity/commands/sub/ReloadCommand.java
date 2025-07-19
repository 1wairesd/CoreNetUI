package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.service.HostCommandService;

public class ReloadCommand {

    private final DiscordBMHPlatformManager platformManager;

    public ReloadCommand(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    public void execute(CommandSource source, MessageContext context, DiscordBMHPlatformManager platformManager, java.nio.file.Path dataDirectory) {
        if (!source.hasPermission("discordbotmanager.reload")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        String result = HostCommandService.reload(dataDirectory, platformManager);
        source.sendMessage(Messages.getComponent(result, context));
    }
}
