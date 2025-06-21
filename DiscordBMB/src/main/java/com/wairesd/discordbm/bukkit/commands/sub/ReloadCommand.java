package com.wairesd.discordbm.bukkit.commands.sub;

import com.wairesd.discordbm.api.event.plugin.DiscordBMReloadEvent;
import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.BukkitPlatformConfig;
import com.wairesd.discordbm.client.common.config.ConfigManager;
import com.wairesd.discordbm.client.common.config.configurators.Messages;
import com.wairesd.discordbm.client.common.platform.PlatformConfig;
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

        PlatformConfig platformConfig = new BukkitPlatformConfig(plugin);
        ConfigManager configManager = new ConfigManager(platformConfig);
        configManager.reloadConfigs();

        if (plugin.getPlatform() != null && 
            plugin.getPlatform().getNettyService() != null &&
            plugin.getPlatform().getNettyService().getNettyClient() != null && 
            plugin.getPlatform().getNettyService().getNettyClient().isActive()) {

            if (DiscordBMB.getApi() != null) {

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
