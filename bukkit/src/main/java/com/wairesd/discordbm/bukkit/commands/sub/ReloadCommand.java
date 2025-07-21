package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.bukkit.DBMBukkitPlugin;
import com.wairesd.discordbm.bukkit.api.BukkitUtils;
import com.wairesd.discordbm.client.common.config.configurators.Messages;
import com.wairesd.discordbm.client.common.service.ClientCommandService;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    private final DBMBukkitPlugin plugin;
    private final ClientCommandService clientCommandService;

    public ReloadCommand(DBMBukkitPlugin plugin) {
        this.plugin = plugin;
        this.clientCommandService = new ClientCommandService(
            DBMBukkitPlugin.getApi(),
            plugin.getConfigManager(),
            plugin.getLogger()
        );
    }

    public boolean execute(CommandSender sender) {
        if (!BukkitUtils.hasPermission(sender, "discordbm.reload")) {
            BukkitUtils.sendMessage(sender, Messages.getMessage(Messages.Keys.NO_PERMISSION));
            return true;
        }
        for (String msg : clientCommandService.reload()) {
            BukkitUtils.sendMessage(sender, msg);
        }
        return true;
    }
} 