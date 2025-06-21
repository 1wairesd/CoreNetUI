package com.wairesd.discordbm.bukkit;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.bukkit.config.ConfigManager;
import com.wairesd.discordbm.bukkit.placeholders.PlaceholderService;
import com.wairesd.discordbm.common.platform.Platform;
import com.wairesd.discordbm.common.utils.DiscordBMThreadPool;
import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;


public class DiscordBMB extends JavaPlugin {
    private final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());
    private static DiscordBMAPI api;
    private ConfigManager configManager;
    private Platform platform;
    private String serverName;
    private boolean invalidSecret = false;
    private PlaceholderService placeholderService;

    private BootstrapDBMB bootstrapService;
    private DiscordBMThreadPool threadPool;

    @Override
    public void onEnable() {
        threadPool = new DiscordBMThreadPool(4);
        bootstrapService = new BootstrapDBMB(this, pluginLogger);
        bootstrapService.initialize();
    }

    @Override
    public void onDisable() {
        if (threadPool != null) {
            threadPool.shutdown();
        }
        if (platform != null && platform.getNettyService() != null) {
            platform.getNettyService().closeNettyConnection();
        }
    }

    public void registerCommandHandler(String command, com.wairesd.discordbm.common.handler.DiscordCommandHandler handler, 
                                      com.wairesd.discordbm.common.listener.DiscordBMCRLB listener, 
                                      com.wairesd.discordbm.common.models.command.Command internalCommand) {
        platform.registerCommandHandler(command, handler, listener, internalCommand);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Platform getPlatform() {
        return platform;
    }

    public static DiscordBMAPI getApi() {
        return api;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public static void setApi(DiscordBMAPI apiInstance) {
        api = apiInstance;
    }

    public DiscordBMThreadPool getThreadPool() {
        return threadPool;
    }

    public PluginLogger getPluginLogger() {
        return pluginLogger;
    }
}
