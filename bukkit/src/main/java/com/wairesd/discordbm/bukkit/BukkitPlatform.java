package com.wairesd.discordbm.bukkit;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.client.common.DiscordBMAPIImpl;
import com.wairesd.discordbm.client.common.platform.AbstractPlatform;
import com.wairesd.discordbm.client.common.platform.PlatformPlaceholder;
import com.wairesd.discordbm.client.common.config.configurators.Settings;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class BukkitPlatform extends AbstractPlatform {
    private final JavaPlugin plugin;

    public BukkitPlatform(JavaPlugin plugin, PlatformPlaceholder platformPlaceholderService, PluginLogger pluginLogger) {
        super(pluginLogger, platformPlaceholderService);
        this.plugin = plugin;
    }

    @Override
    public void runTaskAsynchronously(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }
    
    @Override
    public void runTaskLaterAsynchronously(Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    public void logAllRegisteredServices() {
        if (!Settings.isDebugRegisteredServices()) {
            return;
        }
        
        pluginLogger.info("=== List of all registered services ===");
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
                pluginLogger.info("  - No registered providers");
            }
        }
        pluginLogger.info("=== End of the list of services ===");
    }

    @Override
    public void onNettyConnected() {
        super.onNettyConnected();
        RegisteredServiceProvider<DiscordBMAPI> provider =
            Bukkit.getServicesManager().getRegistration(DiscordBMAPI.class);
        if (provider != null && provider.getProvider() instanceof DiscordBMAPIImpl) {
            DiscordBMAPIImpl impl = (DiscordBMAPIImpl) provider.getProvider();
            impl.getEphemeralRulesManager().resendAllEphemeralRules();
        }
    }
} 