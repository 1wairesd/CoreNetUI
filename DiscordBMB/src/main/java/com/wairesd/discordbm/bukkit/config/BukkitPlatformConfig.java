package com.wairesd.discordbm.bukkit.config;

import com.wairesd.discordbm.client.common.platform.PlatformConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;

public class BukkitPlatformConfig implements PlatformConfig {
    
    private final JavaPlugin plugin;
    
    public BukkitPlatformConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
    
    @Override
    public InputStream getResource(String resourceName) {
        return plugin.getResource(resourceName);
    }
    
    @Override
    public void saveResource(String resourceName, boolean replace) {
        plugin.saveResource(resourceName, replace);
    }
    
    @Override
    public String getPluginName() {
        return plugin.getName();
    }
    
    @Override
    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public void logInfo(String message) {
        plugin.getLogger().info(message);
    }
    
    @Override
    public void logError(String message, Throwable throwable) {
        if (throwable == null) {
            plugin.getLogger().severe(message);
        } else {
            plugin.getLogger().severe(message + ": " + throwable.getMessage());
            throwable.printStackTrace();
        }
    }
    
    @Override
    public void logWarning(String message) {
        plugin.getLogger().warning(message);
    }
} 