package com.wairesd.discordbm.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.velocity.commands.CommandAdmin;
import com.wairesd.discordbm.velocity.commands.commandbuilder.CommandManager;
import com.wairesd.discordbm.velocity.commands.commandbuilder.listeners.buttons.ButtonInteractionListener;
import com.wairesd.discordbm.velocity.commands.commandbuilder.listeners.modals.ModalInteractionListener;
import com.wairesd.discordbm.velocity.config.ConfigManager;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.config.configurators.Commands;
import com.wairesd.discordbm.velocity.database.DatabaseManager;
import com.wairesd.discordbm.velocity.discord.DiscordBotListener;
import com.wairesd.discordbm.velocity.discord.DiscordBotManager;
import com.wairesd.discordbm.velocity.discord.response.ResponseHandler;
import com.wairesd.discordbm.velocity.network.NettyServer;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Plugin(id = "discordbmv", name = "DiscordBMV", version = "1.0", authors = {"wairesd"})
public class DiscordBMV {
    private final Logger logger;
    private final Path dataDirectory;
    private final ProxyServer proxy;
    private NettyServer nettyServer;
    private DatabaseManager dbManager;
    private CommandManager commandManager;
    private DiscordBotManager discordBotManager;
    private Map<String, String> globalMessageLabels = new HashMap<>();
    private final Map<String, Object> formHandlers = new ConcurrentHashMap<>();
    public static DiscordBMV plugin;

    @Inject
    public DiscordBMV(Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxy) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.proxy = proxy;
        this.discordBotManager = new DiscordBotManager(logger);
        Commands.plugin = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        plugin = this;
        Commands.plugin = this;
        initializeConfiguration();
        initializeDatabase();
        initializeNettyServer();
        registerCommands();
        initializeDiscordBot();
        startNettyServer();
    }

    private void initializeDiscordBot() {
        String token = Settings.getBotToken();
        String activityType = Settings.getActivityType();
        String activityMessage = Settings.getActivityMessage();
        discordBotManager.initializeBot(token, activityType, activityMessage);

        JDA jda = discordBotManager.getJda();
        if (jda == null) {
            logger.error("Failed to initialize Discord bot! Aborting further initialization.");
            return;
        }

        logger.info("Discord bot initialized, setting up listeners and commands");
        jda.addEventListener(new ButtonInteractionListener());
        jda.addEventListener(new ModalInteractionListener());
        nettyServer.setJda(jda);
        DiscordBotListener listener = new DiscordBotListener(this, nettyServer, logger);
        jda.addEventListener(listener);
        ResponseHandler.init(listener, logger);
        commandManager = new CommandManager(nettyServer, jda);
        commandManager.loadAndRegisterCommands();

        if (Settings.isDebugCustomCommandRegistrations()) {
            jda.retrieveCommands().queue(commands -> {
                logger.info("Registered commands in Discord: {}",
                        commands.stream().map(cmd -> cmd.getName()).collect(Collectors.toList()));
            }, failure -> {
                logger.error("Failed to retrieve registered commands: {}", failure.getMessage());
            });
        } else {
            jda.retrieveCommands().queue(null, failure -> {
                logger.error("Failed to retrieve registered commands: {}", failure.getMessage());
            });
        }
    }

    private void initializeConfiguration() {
        ConfigManager.init(dataDirectory);
        logger.info("Configuration initialized");
    }

    private void initializeDatabase() {
        String dbPath = "jdbc:sqlite:" + dataDirectory.resolve("DiscordBMV.db");
        dbManager = new DatabaseManager(dbPath);
        logger.info("Database initialized at {}", dbPath);
    }

    private void initializeNettyServer() {
        nettyServer = new NettyServer(logger, dbManager);
        logger.info("Netty server initialized");
    }

    private void startNettyServer() {
        new Thread(nettyServer::start, "Netty-Server-Thread").start();
        logger.info("Netty server thread started");
    }

    private void registerCommands() {
        proxy.getCommandManager().register(
                proxy.getCommandManager().metaBuilder("discordBMV").build(),
                new CommandAdmin(this)
        );
        logger.info("Proxy commands registered");
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

    public DiscordBotManager getDiscordBotManager() {
        return discordBotManager;
    }

    public NettyServer getNettyServer() {
        return nettyServer;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        globalMessageLabels.put(key, channelId + ":" + messageId);
    }

    public String[] getMessageReference(String key) {
        String value = globalMessageLabels.get(key);
        if (value == null) return null;
        return value.contains(":") ? value.split(":", 2) : new String[]{null, value};
    }

    public String getGlobalMessageLabel(String key) {
        return globalMessageLabels.get(key);
    }

    public Map<String, Object> getFormHandlers() {
        return formHandlers;
    }

    public static DiscordBMV getPluginInstance() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }
}