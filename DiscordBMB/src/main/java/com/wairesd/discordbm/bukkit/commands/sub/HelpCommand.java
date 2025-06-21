package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.client.common.config.configurators.Messages;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    private final DiscordBMB plugin;

    public HelpCommand(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(Messages.getMessage(Messages.Keys.HELP_HEADER));
        sender.sendMessage(Messages.getMessage(Messages.Keys.HELP_RELOAD));
        sender.sendMessage(Messages.getMessage(Messages.Keys.HELP_INFO));
        return true;
    }
}
