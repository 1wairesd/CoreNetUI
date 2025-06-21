package com.wairesd.discordbm.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.host.common.api.DiscordHost;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandManager;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.discord.DiscordBotManager;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.host.common.config.configurators.Pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DiscordBMVHost implements DiscordHost {
    
    private final DiscordBMV plugin;
    
    public DiscordBMVHost(DiscordBMV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public Map<UUID, Object> getPendingButtonRequests() {
        return (Map)DiscordBMV.pendingButtonRequests;
    }
    
    @Override
    public Map<String, Page> getPageMap() {
        return Pages.pageMap;
    }
    
    @Override
    public NettyServer getNettyServer() {
        return plugin.getNettyServer();
    }
    
    @Override
    public DiscordBotManager getDiscordBotManager() {
        return plugin.getDiscordBotManager();
    }
    
    @Override
    public CommandManager getCommandManager() {
        return plugin.getCommandManager();
    }
    
    @Override
    public Map<String, Object> getFormHandlers() {
        return plugin.getFormHandlers();
    }
    
    @Override
    public void updateActivity() {
        plugin.updateActivity();
    }
    
    @Override
    public ProxyServer getVelocityProxy() {
        return plugin.getProxy();
    }
    
    @Override
    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        plugin.setGlobalMessageLabel(key, channelId, messageId);
    }
    
    @Override
    public String getGlobalMessageLabel(String key) {
        return plugin.getGlobalMessageLabel(key);
    }
    
    @Override
    public String[] getMessageReference(String key) {
        return plugin.getMessageReference(key);
    }
    
    @Override
    public List<String[]> getAllMessageReferences(String labelPrefix, String guildId) {
        return plugin.getAllMessageReferences(labelPrefix, guildId);
    }
    
    @Override
    public void removeGlobalMessageLabel(String key) {
        plugin.removeGlobalMessageLabel(key);
    }
} 