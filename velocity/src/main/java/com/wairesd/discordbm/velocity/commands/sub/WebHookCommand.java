package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.common.utils.color.transform.AnsiColorTranslator;

public class WebHookCommand {
    
    public void execute(CommandSource source, String[] args, MessageContext context, java.nio.file.Path dataDirectory) {
        if (!source.hasPermission("discordbotmanager.webhook")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        if (args.length < 3) {
            source.sendMessage(Messages.getComponent(Messages.Keys.HELP_WEBHOOK, context));
            return;
        }
        String webhookName = args[1];
        boolean enable = Boolean.parseBoolean(args[2]);
        String result = HostCommandService.toggleWebhook(dataDirectory, webhookName, enable);
        if (context == MessageContext.CONSOLE) {
            source.sendMessage(Component.text(AnsiColorTranslator.translate(result)));
        } else {
            source.sendMessage(ColorUtils.parseComponent(result));
        }
    }
}
