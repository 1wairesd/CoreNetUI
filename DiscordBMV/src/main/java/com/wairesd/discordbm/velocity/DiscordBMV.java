package com.wairesd.discordbm.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.velocity.commands.CommandAdmin;
import com.wairesd.discordbm.velocity.commands.commandbuilder.CommandManager;
import com.wairesd.discordbm.velocity.config.ConfigManager;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.database.DatabaseManager;
import com.wairesd.discordbm.velocity.discord.DiscordBotListener;
import com.wairesd.discordbm.velocity.discord.DiscordBotManager;
import com.wairesd.discordbm.velocity.discord.ResponseHandler;
import com.wairesd.discordbm.velocity.network.NettyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "discordbmv", name = "DiscordBMV", version = "1.0", authors = {"wairesd"})
public class DiscordBMV {
    private final Logger logger;
    private final Path dataDirectory;
    private final ProxyServer proxy;
    private NettyServer nettyServer;
    private DatabaseManager dbManager;
    private CommandManager commandManager;
    private DiscordBotManager discordBotManager;

    @Inject
    public DiscordBMV(Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxy) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.proxy = proxy;
        this.discordBotManager = new DiscordBotManager(logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        initializeConfiguration();
        initializeDatabase();
        initializeNettyServer();
        registerCommands();
        initializeDiscordBot();
        startNettyServer();
    }

    private void initializeConfiguration() {
        ConfigManager.init(dataDirectory);
    }

    private void initializeDatabase() {
        String dbPath = "jdbc:sqlite:" + dataDirectory.resolve("DiscordBMV.db");
        dbManager = new DatabaseManager(dbPath);
    }

    private void initializeNettyServer() {
        nettyServer = new NettyServer(logger, dbManager);
    }

    private void startNettyServer() {
        new Thread(nettyServer::start, "Netty-Server-Thread").start();
    }

    private void registerCommands() {
        proxy.getCommandManager().register(
                proxy.getCommandManager().metaBuilder("discordBMV").build(),
                new CommandAdmin(this)
        );
    }

    private void initializeDiscordBot() {
        String token = Settings.getBotToken();
        String activityType = Settings.getActivityType();
        String activityMessage = Settings.getActivityMessage();

        discordBotManager.initializeBot(token, activityType, activityMessage);
        nettyServer.setJda(discordBotManager.getJda());

        DiscordBotListener listener = new DiscordBotListener(this, nettyServer, logger);
        discordBotManager.getJda().addEventListener(listener);

        ResponseHandler.init(listener, logger);

        commandManager = new CommandManager(nettyServer, discordBotManager.getJda());
        commandManager.loadAndRegisterCommands();
    }

    public void updateActivity() {
        String activityType = Settings.getActivityType();
        String activityMessage = Settings.getActivityMessage();
        discordBotManager.updateActivity(activityType, activityMessage);
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public NettyServer getNettyServer() {
        return nettyServer;
    }

    public Logger getLogger() {
        return logger;
    }
}
