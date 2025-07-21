package com.wairesd.discordbm.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.common.utils.BannerPrinter;
import com.wairesd.discordbm.common.utils.StartupTimer;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.config.configurators.Pages;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import com.wairesd.discordbm.host.common.bootstrap.DiscordBMHBootstrap;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.velocity.commands.CommandAdmin;
import com.wairesd.discordbm.host.common.scheduler.WebhookScheduler;
import com.wairesd.discordbm.velocity.libraries.LibraryLoader;
import com.wairesd.discordbm.velocity.listener.PlayerJoinListener;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import com.velocitypowered.api.plugin.PluginManager;
import org.slf4j.Logger;

@Plugin(id = "discordbm", name = "DiscordBM", version = "1.0", authors = {"wairesd"})
public class DBMVelocityPlugin {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private final Path dataDirectory;
    private final ProxyServer proxy;
    private final PluginManager pluginManager;
    private final Logger slf4jLogger;
    
    private DiscordBMHPlatformManager platformManager;
    private DiscordBMHBootstrap platformBootstrap;
    
    public static DBMVelocityPlugin plugin;
    public static Map<String, Page> pageMap = Pages.pageMap;

    @Inject
    public DBMVelocityPlugin(@DataDirectory Path dataDirectory, ProxyServer proxy, PluginManager pluginManager, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.proxy = proxy;
        this.pluginManager = pluginManager;
        this.slf4jLogger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LibraryLoader loader = new LibraryLoader(proxy, pluginManager, dataDirectory, slf4jLogger, this);
        loader.loadLibraries();
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignored) {}
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException ignored) {}
        StartupTimer timer = new StartupTimer(logger);
        timer.start();
        BannerPrinter.printBanner(BannerPrinter.Platform.VELOCITY);
        plugin = this;
        PluginLogger pluginLogger = new Slf4jPluginLogger(slf4jLogger);
        platformManager = new DiscordBMHPlatformManager(proxy, pluginLogger, Pages.pageMap);
        Commands.setPlatform(platformManager);
        platformBootstrap = new DiscordBMHBootstrap(platformManager, dataDirectory, logger);
        registerCommands();
        proxy.getEventManager().register(this, new PlayerJoinListener());
        platformBootstrap.initialize();
        timer.stop();
        timer.printElapsed();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        WebhookScheduler.shutdown();
        if (platformManager != null) {
            if (platformManager.getNettyServer() != null) platformManager.getNettyServer().shutdown();
            if (platformManager.getDiscordBotManager() != null) platformManager.getDiscordBotManager().shutdown();
        }
        if (platformBootstrap != null && platformBootstrap.getDatabase() != null) {
            platformBootstrap.getDatabase().close();
        }
    }

    private void registerCommands() {
        proxy.getCommandManager().register(
            proxy.getCommandManager().metaBuilder("DiscordBMVelocity")
                .aliases("dbmv")
                .plugin(this)
                .build(),
            new CommandAdmin(platformManager)
        );
    }

    public DiscordBMHPlatformManager getPlatformManager() {
        return platformManager;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}