package com.wairesd.discordbm.bukkit.commands.sub;

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

        if (plugin.getPlatform() != null && 
            plugin.getPlatform().getNettyService() != null &&
            plugin.getPlatform().getNettyService().getNettyClient() != null && 
            plugin.getPlatform().getNettyService().getNettyClient().isActive()) {
            
            // Fire reload event using the new API
            if (DiscordBMB.getApi() != null) {
                // Fire reload events
                DiscordBMReloadEvent configEvent = new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.CONFIG);
                DiscordBMReloadEvent networkEvent = new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.NETWORK);
                DiscordBMReloadEvent commandsEvent = new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.COMMANDS);
                
                DiscordBMB.getApi().getEventRegistry().fireEvent(configEvent);
                DiscordBMB.getApi().getEventRegistry().fireEvent(networkEvent);
                DiscordBMB.getApi().getEventRegistry().fireEvent(commandsEvent);
                
                plugin.getPluginLogger().info("Fired reload events");
            }
        }

        sender.sendMessage(Messages.getMessage(Messages.Keys.RELOAD_SUCCESS));
        return true;
    }
}
