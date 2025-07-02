package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.manager.WebhookManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.scheduler.WebhookScheduler;
import com.wairesd.discordbm.velocity.DiscordBMV;

public class WebHookCommand {
    
    public void execute(CommandSource source, String[] args, MessageContext context) {
        if (!source.hasPermission("discordbotmanager.webhook")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        if (args.length < 3) {
            source.sendMessage(Messages.getComponent(Messages.Keys.HELP_WEBHOOK, context));
            return;
        }

        WebhookScheduler.shutdown();
        String webhookName = args[1];
        boolean enable = Boolean.parseBoolean(args[2]);
        String result = WebhookManager.handleWebhookToggle(DiscordBMV.plugin.getDataDirectory(), webhookName, enable);
        WebhookScheduler.start();
        source.sendMessage(Messages.getComponent(result, context));
    }
}
