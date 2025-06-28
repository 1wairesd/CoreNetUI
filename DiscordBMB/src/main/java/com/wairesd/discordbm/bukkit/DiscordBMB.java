package com.wairesd.discordbm.bukkit;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.client.common.config.ConfigManager;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.client.common.platform.PlatformBootstrap;
import com.wairesd.discordbm.common.utils.DiscordBMThreadPool;
import com.wairesd.discordbm.common.utils.StartupTimer;
import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordBMB extends JavaPlugin {
    private final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());
    private static DiscordBMAPI api;
    private ConfigManager configManager;
    private Platform platform;
    private String serverName;
    private DiscordBMThreadPool threadPool;
    private PlatformBootstrap bootstrap;

    @Override
    public void onEnable() {
        StartupTimer timer = new StartupTimer(pluginLogger);
        timer.start();
        threadPool = new DiscordBMThreadPool(4);
        bootstrap = new BootstrapDBMB(this, pluginLogger);
        bootstrap.initialize();
        platform = bootstrap.getPlatform();
        configManager = bootstrap.getConfigManager();
        api = bootstrap.getApi();
        if (platform instanceof BukkitPlatform) {
            ((BukkitPlatform) platform).logAllRegisteredServices();
        }
        timer.stop();
        timer.printElapsed();
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

    public void registerCommandHandler(String command, CommandHandler handler,
                                      com.wairesd.discordbm.client.common.listener.DiscordBMCRLB listener,
                                      com.wairesd.discordbm.client.common.models.command.Command internalCommand) {
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
