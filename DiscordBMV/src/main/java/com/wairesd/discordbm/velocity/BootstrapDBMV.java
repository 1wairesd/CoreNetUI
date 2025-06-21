package com.wairesd.discordbm.velocity;

import com.wairesd.discordbm.common.utils.BannerPrinter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandManager;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.listener.ButtonInteractionListener;
import com.wairesd.discordbm.host.common.commandbuilder.components.forms.listener.FormInteractionListener;
import com.wairesd.discordbm.velocity.commands.CommandAdmin;
import com.wairesd.discordbm.host.common.config.ConfigManager;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.DatabaseManager;
import com.wairesd.discordbm.host.common.discord.DiscordBotListener;
import com.wairesd.discordbm.host.common.discord.DiscordBotManager;
import com.wairesd.discordbm.host.common.discord.response.ResponseHandler;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.JDA;

import java.nio.file.Path;

public class BootstrapDBMV {
    private final DiscordBMV plugin;
    private final Path dataDirectory;
    private final ProxyServer proxy;
    private final PluginLogger logger;
    private final DiscordBMVHost discordHost;

    private NettyServer nettyServer;
    private DatabaseManager dbManager;
    private CommandManager commandManager;
    private DiscordBotManager discordBotManager;

    public BootstrapDBMV(DiscordBMV plugin, Path dataDirectory, ProxyServer proxy, PluginLogger logger) {
        this.plugin = plugin;
        this.dataDirectory = dataDirectory;
        this.proxy = proxy;
        this.logger = logger;
        this.discordBotManager = new DiscordBotManager();
        this.discordHost = plugin.getDiscordHost();
    }

    public void initialize() {
        BannerPrinter.printBanner(BannerPrinter.Platform.VELOCITY);

        initConfig();
        initDatabase();
        initNetty();
        initCommands();
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
        logger.info("Netty server initialized");
    }

    private void initCommands() {
        proxy.getCommandManager().register(
                proxy.getCommandManager().metaBuilder("discordBMV").build(),
                new CommandAdmin(plugin)
        );
    }

    private void initDiscord() {
        String token = Settings.getBotToken();
        String activityType = Settings.getActivityType();
        String activityMessage = Settings.getActivityMessage();
        discordBotManager.initializeBot(token, activityType, activityMessage);

        JDA jda = discordBotManager.getJda();
        if (jda == null) {
            logger.error("Failed to initialize Discord bot! Aborting.");
            return;
        }

        logger.info("Discord bot initialized");
        jda.addEventListener(new ButtonInteractionListener(nettyServer, discordHost));
        jda.addEventListener(new FormInteractionListener(discordHost));

        nettyServer.setJda(jda);
        DiscordBotListener listener = new DiscordBotListener(discordHost, nettyServer, logger);
        jda.addEventListener(listener);
        ResponseHandler.init(listener, discordHost);

        commandManager = new CommandManager(nettyServer, jda);
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