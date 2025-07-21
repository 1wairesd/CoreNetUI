package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.common.utils.color.transform.AnsiColorTranslator;
import com.wairesd.discordbm.velocity.api.VelocityCommandSender;

public class WebHookCommand {
    
    public void execute(CommandSource source, String[] args, MessageContext context, java.nio.file.Path dataDirectory) {
        VelocityCommandSender sender = new VelocityCommandSender(source);
        if (!source.hasPermission("discordbotmanager.webhook")) {
            sender.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(Messages.getComponent(Messages.Keys.HELP_WEBHOOK, context));
            return;
        }
        String webhookName = args[1];
        boolean enable = Boolean.parseBoolean(args[2]);
        String result = HostCommandService.toggleWebhook(dataDirectory, webhookName, enable);
        if (context == MessageContext.CONSOLE) {
            sender.sendMessage(Component.text(AnsiColorTranslator.translate(result)));
        } else {
            sender.sendMessage(ColorUtils.parseComponent(result));
        }
    }
}
