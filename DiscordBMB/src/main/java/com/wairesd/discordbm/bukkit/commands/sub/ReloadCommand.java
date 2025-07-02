package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.client.common.config.configurators.Messages;
import com.wairesd.discordbm.client.common.service.ClientCommandService;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    private final DiscordBMB plugin;
    private final ClientCommandService clientCommandService;

    public ReloadCommand(DiscordBMB plugin) {
        this.plugin = plugin;
        this.clientCommandService = new ClientCommandService(
            DiscordBMB.getApi(),
            plugin.getConfigManager(),
            plugin.getLogger()
        );
    }

    public boolean execute(CommandSender sender) {
        if (!sender.hasPermission("discordbmb.reload")) {
            sender.sendMessage(Messages.getMessage(Messages.Keys.NO_PERMISSION));
            return true;
        }
        for (String msg : clientCommandService.reload()) {
            sender.sendMessage(msg);
        }
        return true;
    }
} 