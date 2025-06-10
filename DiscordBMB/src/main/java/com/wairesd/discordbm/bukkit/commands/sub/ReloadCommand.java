package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.event.plugin.DiscordBMReloadEvent;
import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.ConfigManager;
import com.wairesd.discordbm.bukkit.config.configurators.Messages;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    private final DiscordBMB plugin;

    public ReloadCommand(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender) {
        if (!sender.hasPermission("discordbmb.reload")) {
            sender.sendMessage(Messages.getMessage(Messages.Keys.NO_PERMISSION));
            return true;
        }

        ConfigManager configManager = new ConfigManager(plugin);
        configManager.reloadConfigs();

        if (plugin.getPlatform() != null && plugin.getNettyService() != null 
            && plugin.getNettyService().getNettyClient() != null 
            && plugin.getNettyService().getNettyClient().isActive()) {
            
            EventBus.post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.NETTY));
            EventBus.post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.COMMANDS));
        }

        sender.sendMessage(Messages.getMessage(Messages.Keys.RELOAD_SUCCESS));
        return true;
    }
}
