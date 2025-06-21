package com.wairesd.discordbm.bukkit;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.bukkit.commands.CommandAdmin;
import com.wairesd.discordbm.bukkit.placeholders.BukkitPlaceholderService;
import com.wairesd.discordbm.client.common.config.ConfigManager;
import com.wairesd.discordbm.client.common.config.configurators.Settings;
import com.wairesd.discordbm.client.common.DiscordBMBAPIImpl;
import com.wairesd.discordbm.client.common.platform.PlatformPlaceholder;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.client.common.platform.PlatformBootstrap;
import com.wairesd.discordbm.common.utils.BannerPrinter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.Bukkit;

public class BootstrapDBMB implements PlatformBootstrap {
    private final DiscordBMB plugin;
    private final PluginLogger logger;
    private final BukkitPlatformConfig platformConfig;
    private final PlatformPlaceholder platformPlaceholderService;
    private Platform platform;
    private ConfigManager configManager;
    private DiscordBMAPI api;
    private com.wairesd.discordbm.client.common.network.NettyService nettyService;

    public BootstrapDBMB(DiscordBMB plugin, PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.platformConfig = new BukkitPlatformConfig(plugin);
        this.platformPlaceholderService = new BukkitPlaceholderService(plugin);
    }

    @Override
    public void initialize() {
        BannerPrinter.printBanner(BannerPrinter.Platform.BUKKIT);

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
        nettyService = new com.wairesd.discordbm.client.common.network.NettyService(() -> platform, logger);
        platform = new BukkitPlatform(plugin, platformPlaceholderService, logger);
        plugin.setPlatform(platform);
        logger.info("Platform initialized");
    }

    private void initApi() {
        api = new DiscordBMBAPIImpl(platform, logger);
        DiscordBMB.setApi(api);
        Bukkit.getServicesManager().register(DiscordBMAPI.class, api, plugin, org.bukkit.plugin.ServicePriority.Normal);
        
        logger.info("DiscordBM API initialized");
    }

    private void registerCommands() {
        plugin.getCommand("DiscordBMB").setExecutor(new CommandAdmin(plugin));
        plugin.getCommand("DiscordBMB").setTabCompleter(new CommandAdmin(plugin));
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