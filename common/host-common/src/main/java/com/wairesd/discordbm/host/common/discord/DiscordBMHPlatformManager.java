package com.wairesd.discordbm.host.common.discord;

import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.DiscordBMThreadPool;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandManager;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.database.Database;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordBMHPlatformManager {
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;

    private final ProxyServer proxyServer;
    private final PluginLogger logger;
    private final DiscordBMThreadPool threadPool;
    private final Map<String, Object> formHandlers;
    private final Map<UUID, InteractionHook> pendingButtonRequests;
    private final MessageManager messageManager;

    private CommandManager commandManager;
    private NettyServer nettyServer;
    private DiscordBotManager discordBotManager;
    private Map<String, Page> pageMap;

    public DiscordBMHPlatformManager(ProxyServer proxyServer, PluginLogger logger, Map<String, Page> pageMap) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.pageMap = pageMap;
        this.threadPool = createThreadPool();
        this.messageManager = new MessageManager();
        this.formHandlers = new ConcurrentHashMap<>();
        this.pendingButtonRequests = new ConcurrentHashMap<>();

        registerShutdownHook();
    }

    public void setNettyServer(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public void attachDatabaseToManagers(Database database) {
        messageManager.setDatabase(database);
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void setDiscordBotManager(DiscordBotManager discordBotManager) {
        this.discordBotManager = discordBotManager;
    }

    public void updateActivity() {
        if (isDiscordBotManagerAvailable()) {
            discordBotManager.updateActivity(
                    Settings.getActivityType(),
                    Settings.getActivityMessage()
            );
        }
    }

    public void storePendingButtonRequest(UUID requestId, InteractionHook hook) {
        pendingButtonRequests.put(requestId, hook);
    }

    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        messageManager.setGlobalMessageLabel(key, channelId, messageId);
    }

    public void removeGlobalMessageLabel(String key) {
        messageManager.removeGlobalMessageLabel(key);
    }

    public void removeMessageReference(String key, String channelId, String messageId) {
        messageManager.removeMessageReference(key, channelId, messageId);
    }

    public Map<UUID, InteractionHook> getPendingButtonRequests() {
        return pendingButtonRequests;
    }

    public Map<String, Page> getPageMap() {
        return pageMap;
    }

    public NettyServer getNettyServer() {
        return nettyServer;
    }

    public DiscordBotManager getDiscordBotManager() {
        return discordBotManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Map<String, Object> getFormHandlers() {
        return formHandlers;
    }

    public ProxyServer getVelocityProxy() {
        return proxyServer;
    }

    public PluginLogger getLogger() {
        return logger;
    }

    public String getGlobalMessageLabel(String key) {
        return messageManager.getGlobalMessageLabel(key);
    }

    public String[] getMessageReference(String key) {
        return messageManager.getMessageReference(key);
    }

    public List<String[]> getAllMessageReferencesByLabel(String key) {
        return messageManager.getAllMessageReferencesByLabel(key);
    }

    @Deprecated
    public List<String[]> getAllMessageReferences(String labelPrefix, String guildId) {
        return messageManager.getAllMessageReferences(labelPrefix, guildId);
    }

    private DiscordBMThreadPool createThreadPool() {
        return new DiscordBMThreadPool(DEFAULT_THREAD_POOL_SIZE);
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownThreadPool));
    }

    private void shutdownThreadPool() {
        if (threadPool != null) {
            threadPool.shutdown();
        }
    }

    private boolean isDiscordBotManagerAvailable() {
        return discordBotManager != null;
    }
}