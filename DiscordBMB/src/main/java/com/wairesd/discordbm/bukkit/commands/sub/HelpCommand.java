package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.client.common.service.ClientCommandService;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    private final DiscordBMB plugin;
    private final ClientCommandService clientCommandService;

    public HelpCommand(DiscordBMB plugin) {
        this.plugin = plugin;
        this.clientCommandService = new ClientCommandService(
            DiscordBMB.getApi(),
            plugin.getConfigManager(),
            plugin.getLogger()
        );
    }

    public boolean execute(CommandSender sender) {
        for (String msg : clientCommandService.getHelp()) {
            sender.sendMessage(msg);
        }
        return true;
    }
} 