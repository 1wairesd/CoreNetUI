package com.wairesd.discordbm.host.common.discord;

import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.common.utils.DiscordBMThreadPool;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandManager;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordBMHPlatformManager {
    private final ProxyServer proxyServer;
    private final PluginLogger logger;
    private final DiscordBMThreadPool threadPool;
    private final Map<String, Object> formHandlers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, InteractionHook> pendingButtonRequests = new ConcurrentHashMap<>();
    
    private final MessageManager messageManager;
    private CommandManager commandManager;
    private NettyServer nettyServer;
    private DiscordBotManager discordBotManager;
    private Map<String, Page> pageMap;

    public DiscordBMHPlatformManager(ProxyServer proxyServer, PluginLogger logger, Map<String, Page> pageMap) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.pageMap = pageMap;
        this.threadPool = new DiscordBMThreadPool(4);
        this.messageManager = new MessageManager();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (threadPool != null) {
                threadPool.shutdown();
            }
        }));
    }

    public void setNettyServer(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void setDiscordBotManager(DiscordBotManager discordBotManager) {
        this.discordBotManager = discordBotManager;
    }

    public void setPageMap(Map<String, Page> pageMap) {
        this.pageMap = pageMap;
    }

    public Map<UUID, Object> getPendingButtonRequests() {
        return (Map)pendingButtonRequests;
    }

    public void storePendingButtonRequest(UUID requestId, InteractionHook hook) {
        pendingButtonRequests.put(requestId, hook);
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

    public void updateActivity() {
        if (discordBotManager != null) {
            discordBotManager.updateActivity(
                Settings.getActivityType(),
                Settings.getActivityMessage()
            );
        }
    }

    public ProxyServer getVelocityProxy() {
        return proxyServer;
    }

    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        messageManager.setGlobalMessageLabel(key, channelId, messageId);
    }

    public String getGlobalMessageLabel(String key) {
        return messageManager.getGlobalMessageLabel(key);
    }

    public String[] getMessageReference(String key) {
        return messageManager.getMessageReference(key);
    }

    @Deprecated
    public List<String[]> getAllMessageReferences(String labelPrefix, String guildId) {
        return messageManager.getAllMessageReferences(labelPrefix, guildId);
    }

    public void removeGlobalMessageLabel(String key) {
        messageManager.removeGlobalMessageLabel(key);
    }

    public PluginLogger getLogger() {
        return logger;
    }
} 