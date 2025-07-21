package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.bukkit.DBMBukkitPlugin;
import com.wairesd.discordbm.bukkit.api.BukkitUtils;
import com.wairesd.discordbm.client.common.service.ClientCommandService;
import org.bukkit.command.CommandSender;

public class HelpCommand {
    private final DBMBukkitPlugin plugin;
    private final ClientCommandService clientCommandService;

    public HelpCommand(DBMBukkitPlugin plugin) {
        this.plugin = plugin;
        this.clientCommandService = new ClientCommandService(
            DBMBukkitPlugin.getApi(),
            plugin.getConfigManager(),
            plugin.getLogger()
        );
    }

    public boolean execute(CommandSender sender) {
        for (String msg : clientCommandService.getHelp()) {
            BukkitUtils.sendMessage(sender, msg);
        }
        return true;
    }
} 