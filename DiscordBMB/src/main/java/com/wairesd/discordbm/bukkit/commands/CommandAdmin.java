package com.wairesd.discordbm.bukkit.commands;

import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.configurators.Messages;
import com.wairesd.discordbm.bukkit.commands.sub.ReloadCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class CommandAdmin implements CommandExecutor, TabCompleter {
    private final DiscordBMB plugin;
    private final ReloadCommand reloadCommand;

    public CommandAdmin(DiscordBMB plugin) {
        this.plugin = plugin;
        this.reloadCommand = new ReloadCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(Messages.getMessage("usage-admin-command"));
            return true;
        }

        return reloadCommand.execute(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return List.of();
    }
}
