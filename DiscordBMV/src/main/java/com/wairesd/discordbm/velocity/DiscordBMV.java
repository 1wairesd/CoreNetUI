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
import com.wairesd.discordbm.velocity.listener.PlayerJoinListener;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

@Plugin(id = "discordbmv", name = "DiscordBMV", version = "1.0", authors = {"wairesd"})
public class DiscordBMV {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final Path dataDirectory;
    private final ProxyServer proxy;
    
    private DiscordBMHPlatformManager platformManager;
    private DiscordBMHBootstrap platformBootstrap;
    
    public static DiscordBMV plugin;
    public static Map<String, Page> pageMap = Pages.pageMap;

    @Inject
    public DiscordBMV(@DataDirectory Path dataDirectory, ProxyServer proxy) {
        this.dataDirectory = dataDirectory;
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        StartupTimer timer = new StartupTimer(logger);
        timer.start();
        BannerPrinter.printBanner(BannerPrinter.Platform.VELOCITY);
        plugin = this;
        platformManager = new DiscordBMHPlatformManager(proxy, logger, Pages.pageMap);
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
        if (platformBootstrap != null) {
            platformBootstrap.shutdown();
        }
    }

    private void registerCommands() {
        proxy.getCommandManager().register(
            proxy.getCommandManager().metaBuilder("DiscordBMV").build(),
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