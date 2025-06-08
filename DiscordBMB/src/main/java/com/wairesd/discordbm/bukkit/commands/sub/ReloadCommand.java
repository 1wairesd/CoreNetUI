package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.configurators.Messages;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    private final DiscordBMB plugin;

    public ReloadCommand(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender) {
        if (!sender.hasPermission("discordbotmanager.reload")) {
            sender.sendMessage(Messages.getMessage("no-permission"));
            return true;
        }

        plugin.getConfigManager().reloadConfigs();
        plugin.getNettyService().closeNettyConnection();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String host = Settings.getVelocityHost();
            int port = Settings.getVelocityPort();
            plugin.getNettyService().initializeNettyClient();
        });

        sender.sendMessage(Messages.getMessage("reload-success"));
        return true;
    }
}
