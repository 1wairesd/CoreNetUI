package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.common.utils.color.transform.AnsiColorTranslator;

public class HelpCommand {

    public void execute(CommandSource source, MessageContext context) {
        if (!source.hasPermission("discordbotmanager.help")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        String result = HostCommandService.getHelp(context);
        if (context == MessageContext.CONSOLE) {
            source.sendMessage(Component.text(AnsiColorTranslator.translate(result)));
        } else {
            source.sendMessage(ColorUtils.parseComponent(result));
        }
    }
}
