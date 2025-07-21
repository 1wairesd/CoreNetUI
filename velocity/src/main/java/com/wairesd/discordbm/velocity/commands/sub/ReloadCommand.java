package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.velocity.api.VelocityCommandSender;

public class ReloadCommand {

    private final DiscordBMHPlatformManager platformManager;

    public ReloadCommand(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    public void execute(CommandSource source, MessageContext context, DiscordBMHPlatformManager platformManager, java.nio.file.Path dataDirectory) {
        VelocityCommandSender sender = new VelocityCommandSender(source);
        if (!source.hasPermission("discordbotmanager.reload")) {
            sender.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context).toString());
            return;
        }
        String result = HostCommandService.reload(dataDirectory, platformManager);
        sender.sendMessage(Messages.getComponent(result, context).toString());
    }
}
