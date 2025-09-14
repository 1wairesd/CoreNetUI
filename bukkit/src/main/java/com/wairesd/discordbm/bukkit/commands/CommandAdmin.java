package com.wairesd.discordbm.bukkit.commands;

import com.wairesd.discordbm.bukkit.DBMBukkitPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdmin implements CommandExecutor {

    public CommandAdmin(DBMBukkitPlugin plugin) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

}