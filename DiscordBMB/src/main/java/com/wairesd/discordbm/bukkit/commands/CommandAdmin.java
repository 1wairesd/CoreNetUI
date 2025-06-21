package com.wairesd.discordbm.bukkit.commands;

import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.commands.sub.HelpCommand;
import com.wairesd.discordbm.bukkit.commands.sub.ReloadCommand;
import com.wairesd.discordbm.client.common.config.configurators.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandAdmin implements CommandExecutor, TabCompleter {
    private final DiscordBMB plugin;
    private final ReloadCommand reloadCommand;
    private final HelpCommand helpCommand;

    public CommandAdmin(DiscordBMB plugin) {
        this.plugin = plugin;
        this.reloadCommand = new ReloadCommand(plugin);
        this.helpCommand = new HelpCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                return reloadCommand.execute(sender);
            case "help":
                return helpCommand.execute(sender);
            default:
                showHelp(sender);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("reload");
            completions.add("help");
            return completions;
        }
        return new ArrayList<>();
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(Messages.getMessage(Messages.Keys.HELP_HEADER));
        sender.sendMessage(Messages.getMessage(Messages.Keys.HELP_RELOAD));
        sender.sendMessage(Messages.getMessage(Messages.Keys.HELP_INFO));
    }
}