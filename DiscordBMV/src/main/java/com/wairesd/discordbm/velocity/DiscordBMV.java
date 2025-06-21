package com.wairesd.discordbm.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.common.utils.BannerPrinter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.config.configurators.Pages;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import com.wairesd.discordbm.host.common.bootstrap.DiscordBMHBootstrap;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.velocity.commands.CommandAdmin;
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
        plugin = this;

        platformManager = new DiscordBMHPlatformManager(proxy, logger, Pages.pageMap);

        Commands.setPlatform(platformManager);

        platformBootstrap = new DiscordBMHBootstrap(
            platformManager, 
            dataDirectory, 
            logger,
            BannerPrinter.Platform.VELOCITY
        );

        registerCommands();

        platformBootstrap.initialize();
    }

    private void registerCommands() {
        proxy.getCommandManager().register(
            proxy.getCommandManager().metaBuilder("discordBMV").build(),
            new CommandAdmin(platformManager)
        );
    }

    public DiscordBMHPlatformManager getPlatformManager() {
        return platformManager;
    }
}