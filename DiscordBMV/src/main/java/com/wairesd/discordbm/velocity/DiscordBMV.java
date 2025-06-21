package com.wairesd.discordbm.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandManager;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.config.configurators.Pages;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.discord.DiscordBotManager;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.LoggerFactory;
import com.wairesd.discordbm.common.utils.DiscordBMThreadPool;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Plugin(id = "discordbmv", name = "DiscordBMV", version = "1.0", authors = {"wairesd"})
public class DiscordBMV {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    public static final ConcurrentHashMap<UUID, InteractionHook> pendingButtonRequests = new ConcurrentHashMap<>();
    private final Path dataDirectory;
    private final ProxyServer proxy;

    private BootstrapDBMV bootstrapService;
    private DiscordBMThreadPool threadPool;

    private final Map<String, String> globalMessageLabels = new HashMap<>();
    private final Map<String, Object> formHandlers = new ConcurrentHashMap<>();

    public static DiscordBMV plugin;
    public static Map<String, Page> pageMap = Pages.pageMap;
    
    private DiscordBMVHost discordHost;

    @Inject
    public DiscordBMV(@DataDirectory Path dataDirectory, ProxyServer proxy) {
        this.dataDirectory = dataDirectory;
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        plugin = this;
        discordHost = new DiscordBMVHost(this);
        Commands.discordHost = discordHost;

        threadPool = new DiscordBMThreadPool(4);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (threadPool != null) {
                threadPool.shutdown();
            }
        }));

        bootstrapService = new BootstrapDBMV(this, dataDirectory, proxy, logger);
        bootstrapService.initialize();
    }

    public void updateActivity() {
        bootstrapService.getDiscordBotManager().updateActivity(
                Settings.getActivityType(),
                Settings.getActivityMessage()
        );
    }

    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        globalMessageLabels.put(key, channelId + ":" + messageId);
    }

    public String[] getMessageReference(String key) {
        String value = globalMessageLabels.get(key);
        if (value == null) return null;
        return value.contains(":") ? value.split(":", 2) : new String[]{null, value};
    }
    
    public void removeGlobalMessageLabel(String key) {
        globalMessageLabels.remove(key);
    }
    
    public List<String[]> getAllMessageReferences(String labelPrefix, String guildId) {
        String fullPrefix = guildId + "_" + labelPrefix;
        List<String[]> results = new ArrayList<>();
        
        // Собираем все метки, которые начинаются с нужного префикса
        for (Map.Entry<String, String> entry : globalMessageLabels.entrySet()) {
            if (entry.getKey().equals(fullPrefix) || 
                    (labelPrefix.isEmpty() && entry.getKey().startsWith(guildId + "_"))) {
                
                String value = entry.getValue();
                if (value != null && value.contains(":")) {
                    String[] parts = value.split(":", 2);
                    results.add(parts);
                }
            }
        }
        
        return results;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public String getGlobalMessageLabel(String key) {
        return globalMessageLabels.get(key);
    }

    public Map<String, Object> getFormHandlers() {
        return formHandlers;
    }

    public PluginLogger getLogger() {
        return logger;
    }

    public NettyServer getNettyServer() {
        return bootstrapService != null ? bootstrapService.getNettyServer() : null;
    }

    public DiscordBotManager getDiscordBotManager() {
        return bootstrapService.getDiscordBotManager();
    }

    public CommandManager getCommandManager() {
        return bootstrapService.getCommandManager();
    }

    public DiscordBMThreadPool getThreadPool() {
        return threadPool;
    }
    
    public DiscordBMVHost getDiscordHost() {
        return discordHost;
    }
}