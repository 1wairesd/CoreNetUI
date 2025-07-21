package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.common.utils.color.transform.AnsiColorTranslator;
import com.wairesd.discordbm.velocity.api.VelocityCommandSender;

public class ClientsCommand {
    private final DiscordBMHPlatformManager platformManager;

    public ClientsCommand(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    public void execute(CommandSource source, MessageContext context, DiscordBMHPlatformManager platformManager) {
        VelocityCommandSender sender = new VelocityCommandSender(source);
        if (!source.hasPermission("discordbotmanager.clients")) {
            sender.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        String result = HostCommandService.listClients(platformManager);
        if (context == MessageContext.CONSOLE) {
            sender.sendMessage(Component.text(AnsiColorTranslator.translate(result)));
        } else {
            sender.sendMessage(ColorUtils.parseComponent(result));
        }
    }
}