package com.wairesd.discordbm.host.common.bootstrap;

import com.wairesd.discordbm.common.utils.BannerPrinter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandManager;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.listener.ButtonInteractionListener;
import com.wairesd.discordbm.host.common.commandbuilder.components.forms.listener.FormInteractionListener;
import com.wairesd.discordbm.host.common.config.ConfigManager;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.DatabaseManager;
import com.wairesd.discordbm.host.common.discord.DiscordBotListener;
import com.wairesd.discordbm.host.common.discord.DiscordBotManager;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.discord.response.ResponseHandler;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.JDA;

import java.nio.file.Path;

public class DiscordBMHBootstrap {
    private final DiscordBMHPlatformManager platformManager;
    private final Path dataDirectory;
    private final PluginLogger logger;
    
    private NettyServer nettyServer;
    private DatabaseManager dbManager;
    private CommandManager commandManager;
    private DiscordBotManager discordBotManager;
    private BannerPrinter.Platform platform;

    public DiscordBMHBootstrap(DiscordBMHPlatformManager platformManager, Path dataDirectory,
                               PluginLogger logger, BannerPrinter.Platform platform) {
        this.platformManager = platformManager;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.platform = platform;
        this.discordBotManager = new DiscordBotManager();
        platformManager.setDiscordBotManager(discordBotManager);
    }

    public void initialize() {
        BannerPrinter.printBanner(platform);

        initConfig();
        initDatabase();
        initNetty();
        initDiscord();
        startNetty();
    }

    private void initConfig() {
        ConfigManager.init(dataDirectory);
        logger.info("Configuration initialized");
    }

    private void initDatabase() {
        String dbPath = "jdbc:sqlite:" + dataDirectory.resolve("DiscordBMV.db");
        dbManager = new DatabaseManager(dbPath);
        logger.info("Database initialized");
    }

    private void initNetty() {
        nettyServer = new NettyServer(dbManager);
        platformManager.setNettyServer(nettyServer);
        logger.info("Netty server is initialized");
    }

    private void initDiscord() {
        String token = Settings.getBotToken();
        String activityType = Settings.getActivityType();
        String activityMessage = Settings.getActivityMessage();
        discordBotManager.initializeBot(token, activityType, activityMessage);

        JDA jda = discordBotManager.getJda();
        if (jda == null) {
            logger.error("Bot Discord initialization failed! Interrupt.");
            return;
        }

        logger.info("Discord bot initialized");
        jda.addEventListener(new ButtonInteractionListener(nettyServer, platformManager));
        jda.addEventListener(new FormInteractionListener(platformManager));

        nettyServer.setJda(jda);
        DiscordBotListener listener = new DiscordBotListener(platformManager, nettyServer, logger);
        jda.addEventListener(listener);
        ResponseHandler.init(listener, platformManager);

        commandManager = new CommandManager(nettyServer, jda);
        platformManager.setCommandManager(commandManager);
        commandManager.loadAndRegisterCommands();
    }

    private void startNetty() {
        new Thread(nettyServer::start, "Netty-Server-Thread").start();
    }

    public DiscordBotManager getDiscordBotManager() {
        return discordBotManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public NettyServer getNettyServer() {
        return nettyServer;
    }
} 