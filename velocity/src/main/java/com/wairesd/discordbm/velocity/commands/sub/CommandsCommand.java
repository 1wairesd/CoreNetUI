package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.common.utils.color.transform.AnsiColorTranslator;

public class CommandsCommand {
    private final DiscordBMHPlatformManager platformManager;

    public CommandsCommand(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    public void execute(CommandSource source, String[] args, MessageContext context, DiscordBMHPlatformManager platformManager) {
        if (!source.hasPermission("discordbotmanager.commands")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        if (args.length < 2) {
            if (context == MessageContext.CONSOLE) {
                source.sendMessage(Component.text(AnsiColorTranslator.translate(HostCommandService.getHelp(context))));
            } else {
                source.sendMessage(ColorUtils.parseComponent(HostCommandService.getHelp(context)));
            }
            return;
        }
        switch (args[1].toLowerCase()) {
            case "custom" -> {
                if (context == MessageContext.CONSOLE) {
                    source.sendMessage(Component.text(AnsiColorTranslator.translate(HostCommandService.getCustomCommands(context))));
                } else {
                    source.sendMessage(ColorUtils.parseComponent(HostCommandService.getCustomCommands(context)));
                }
            }
            case "addons" -> {
                if (context == MessageContext.CONSOLE) {
                    source.sendMessage(Component.text(AnsiColorTranslator.translate(HostCommandService.getAddonCommands(platformManager, context))));
                } else {
                    source.sendMessage(ColorUtils.parseComponent(HostCommandService.getAddonCommands(platformManager, context)));
                }
            }
            default -> {
                if (context == MessageContext.CONSOLE) {
                    source.sendMessage(Component.text(AnsiColorTranslator.translate(HostCommandService.getHelp(context))));
                } else {
                    source.sendMessage(ColorUtils.parseComponent(HostCommandService.getHelp(context)));
                }
            }
        }
    }
}
