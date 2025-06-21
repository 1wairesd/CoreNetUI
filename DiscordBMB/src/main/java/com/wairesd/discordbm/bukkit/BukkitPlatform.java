package com.wairesd.discordbm.bukkit;

import com.wairesd.discordbm.client.common.handler.DiscordCommandHandler;
import com.wairesd.discordbm.client.common.listener.DiscordBMCRLB;
import com.wairesd.discordbm.client.common.models.command.Command;
import com.wairesd.discordbm.client.common.network.NettyService;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.client.common.config.configurators.Settings;
import com.wairesd.discordbm.client.common.placeholders.PlaceholderService;
import com.wairesd.discordbm.client.common.platform.PlatformPlaceholder;
import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.*;

public class BukkitPlatform implements Platform {
    private final DiscordBMB plugin;
    private final NettyService nettyService;
    private final Map<String, DiscordCommandHandler> commandHandlers = new HashMap<>();
    private final PlaceholderService placeholderService;
    private final PluginLogger pluginLogger;
    private final Set<DiscordBMCRLB> listeners = new HashSet<>();
    private final List<Command> addonCommands = new ArrayList<>();

    public BukkitPlatform(DiscordBMB plugin, PlatformPlaceholder platformPlaceholderService) {
        this.plugin = plugin;
        this.pluginLogger = new JavaPluginLogger(Bukkit.getLogger());
        this.nettyService = new NettyService(this, pluginLogger);
        this.placeholderService = new PlaceholderService(platformPlaceholderService);
    }

    @Override
    public String getVelocityHost() {
        return Settings.getVelocityHost();
    }

    @Override
    public int getVelocityPort() {
        return Settings.getVelocityPort();
    }

    @Override
    public String getServerName() {
        return Settings.getServerName();
    }

    @Override
    public String getSecretCode() {
        return Settings.getSecretCode();
    }

    @Override
    public boolean isDebugCommandRegistrations() {
        return Settings.isDebugCommandRegistrations();
    }

    @Override
    public boolean isDebugClientResponses() {
        return Settings.isDebugClientResponses();
    }

    @Override
    public boolean isDebugConnections() {
        return Settings.isDebugConnections();
    }

    @Override
    public boolean isDebugErrors() {
        return Settings.isDebugErrors();
    }

    @Override
    public NettyService getNettyService() {
        return nettyService;
    }

    @Override
    public void registerCommandHandler(String command, DiscordCommandHandler handler, DiscordBMCRLB listener, Command addonCommand) {
        commandHandlers.put(command, handler);
        if (addonCommand != null) {
            synchronized (addonCommands) {
                addonCommands.add(addonCommand);
                if (Settings.isDebugCommandRegistrations()) {
                    pluginLogger.info("Registered addon command: " + addonCommand.getName());
                }
            }
        }
        if (listener != null && listeners.add(listener)) {
            if (nettyService.getNettyClient() != null && nettyService.getNettyClient().isActive()) {
                listener.onNettyConnected();
            }
        }
    }

    @Override
    public void onNettyConnected() {
        for (DiscordBMCRLB listener : listeners) {
            listener.onNettyConnected();
        }

        if (listeners.isEmpty()) {
            List<Command> commands = getAddonCommands();
            if (!commands.isEmpty() && nettyService.getNettyClient() != null) {
                nettyService.registerCommands(commands);
            }
        }
    }

    @Override
    public Map<String, DiscordCommandHandler> getCommandHandlers() {
        return Collections.unmodifiableMap(commandHandlers);
    }

    @Override
    public boolean checkIfCanHandle(String playerName, List<String> placeholders) {
        return placeholderService.checkIfCanHandle(playerName, placeholders);
    }

    @Override
    public Map<String, String> getPlaceholderValues(String playerName, List<String> placeholders) {
        return placeholderService.getPlaceholderValues(playerName, placeholders);
    }

    @Override
    public void runTaskAsynchronously(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    public List<Command> getAddonCommands() {
        synchronized (addonCommands) {
            return new ArrayList<>(addonCommands);
        }
    }

    public void logAllRegisteredServices() {
        pluginLogger.info("=== Listing all registered services ===");
        for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
            @SuppressWarnings("unchecked")
            Collection<RegisteredServiceProvider<?>> providers = 
                (Collection<RegisteredServiceProvider<?>>) Bukkit.getServicesManager().getRegistrations(service);
            
            pluginLogger.info("Service: " + service.getName());
            if (providers != null) {
                for (RegisteredServiceProvider<?> provider : providers) {
                    pluginLogger.info("  - Provider: " + provider.getService().getName() + 
                                   ", Plugin: " + provider.getPlugin().getName() +
                                   ", Priority: " + provider.getPriority());
                }
            } else {
                pluginLogger.info("  - No providers registered");
            }
        }
        pluginLogger.info("=== End of service listing ===");
    }
}