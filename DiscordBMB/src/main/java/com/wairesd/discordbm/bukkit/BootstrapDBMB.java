package com.wairesd.discordbm.bukkit;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.bukkit.commands.CommandAdmin;
import com.wairesd.discordbm.bukkit.config.BukkitPlatformConfig;
import com.wairesd.discordbm.bukkit.placeholders.BukkitPlaceholderService;
import com.wairesd.discordbm.client.common.config.ConfigManager;
import com.wairesd.discordbm.client.common.config.configurators.Settings;
import com.wairesd.discordbm.client.common.DiscordBMBAPIImpl;
import com.wairesd.discordbm.client.common.platform.PlatformPlaceholder;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.client.common.platform.PlatformConfig;
import com.wairesd.discordbm.common.utils.BannerPrinter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.Bukkit;


public class BootstrapDBMB {
    private final DiscordBMB plugin;
    private final PluginLogger logger;
    private final PlatformConfig platformConfig;
    private final PlatformPlaceholder platformPlaceholderService;

    public BootstrapDBMB(DiscordBMB plugin, PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.platformConfig = new BukkitPlatformConfig(plugin);
        this.platformPlaceholderService = new BukkitPlaceholderService(plugin);
    }

    public void initialize() {
        BannerPrinter.printBanner(BannerPrinter.Platform.BUKKIT);

        initConfig();
        initPlatform();
        initApi();
        registerCommands();
        initNettyAsync();
    }

    private void initConfig() {
        ConfigManager configManager = new ConfigManager(platformConfig);
        configManager.loadConfigs();
        plugin.setConfigManager(configManager);
        plugin.setServerName(Settings.getServerName());
        logger.info("Configuration initialized");
    }

    private void initPlatform() {
        Platform platform = new BukkitPlatform(plugin, platformPlaceholderService);
        plugin.setPlatform(platform);
        logger.info("Platform initialized");
    }

    private void initApi() {
        DiscordBMAPI api = new DiscordBMBAPIImpl(plugin.getPlatform(), logger);
        DiscordBMB.setApi(api);

        Bukkit.getServicesManager().register(DiscordBMAPI.class, api, plugin, org.bukkit.plugin.ServicePriority.Normal);

        if (plugin.getPlatform() instanceof BukkitPlatform) {
            ((BukkitPlatform) plugin.getPlatform()).logAllRegisteredServices();
        }
        
        logger.info("DiscordBM API initialized");
    }

    private void registerCommands() {
        plugin.getCommand("DiscordBMB").setExecutor(new CommandAdmin(plugin));
        plugin.getCommand("DiscordBMB").setTabCompleter(new CommandAdmin(plugin));
        logger.info("Commands registered");
    }

    private void initNettyAsync() {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPlatform().getNettyService().initializeNettyClient();
            logger.info("Netty client initialized asynchronously");
        });
    }
}
