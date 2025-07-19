package com.wairesd.discordbm.bukkit;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.bukkit.commands.CommandAdmin;
import com.wairesd.discordbm.bukkit.placeholders.BukkitPlaceholderService;
import com.wairesd.discordbm.client.common.config.ConfigManager;
import com.wairesd.discordbm.client.common.config.configurators.Settings;
import com.wairesd.discordbm.client.common.DiscordBMAPIImpl;
import com.wairesd.discordbm.client.common.network.NettyService;
import com.wairesd.discordbm.client.common.platform.PlatformPlaceholder;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.client.common.platform.PlatformBootstrap;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.Bukkit;

public class DBMBukkitBootstrap implements PlatformBootstrap {
    private final DBMBukkitPlugin plugin;
    private final PluginLogger logger;
    private final BukkitPlatformConfig platformConfig;
    private final PlatformPlaceholder platformPlaceholderService;
    private Platform platform;
    private ConfigManager configManager;
    private DiscordBMAPI api;
    private NettyService nettyService;

    public DBMBukkitBootstrap(DBMBukkitPlugin plugin, PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.platformConfig = new BukkitPlatformConfig(plugin);
        this.platformPlaceholderService = new BukkitPlaceholderService(plugin);
    }

    @Override
    public void initialize() {
        initConfig();
        initPlatform();
        initApi();
        registerCommands();
        initNettyAsync();
    }

    private void initConfig() {
        configManager = new ConfigManager(platformConfig);
        configManager.loadConfigs();
        plugin.setConfigManager(configManager);
        plugin.setServerName(Settings.getServerName());
        logger.info("Configuration initialized");
    }

    private void initPlatform() {
        nettyService = new NettyService(() -> platform, logger);
        platform = new BukkitPlatform(plugin, platformPlaceholderService, logger);
        plugin.setPlatform(platform);
        logger.info("Platform initialized");
    }

    private void initApi() {
        api = new DiscordBMAPIImpl(platform, logger);
        com.wairesd.discordbm.api.DiscordBMAPIProvider.setInstance(api);
        DBMBukkitPlugin.setApi(api);
        Bukkit.getServicesManager().register(DiscordBMAPI.class, api, plugin, org.bukkit.plugin.ServicePriority.Normal);
        logger.info("DiscordBM API initialized");
    }

    private void registerCommands() {
        plugin.getCommand("DiscordBM").setExecutor(new CommandAdmin(plugin));
        plugin.getCommand("DiscordBM").setTabCompleter(new CommandAdmin(plugin));
        logger.info("Commands registered");
    }

    private void initNettyAsync() {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            platform.getNettyService().initializeNettyClient();
            logger.info("Netty client initialized asynchronously");
        });
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public DiscordBMAPI getApi() {
        return api;
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }
} 