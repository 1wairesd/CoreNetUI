package com.wairesd.discordbm.bukkit.config;

import com.wairesd.discordbm.bukkit.config.configurators.Messages;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The ConfigManager class manages the loading and reloading of configuration files
 * used in the plugin. It is responsible for initializing and refreshing the
 * configuration data for the plugin's functionality.
 *
 * The plugin's configuration files should be available and properly formatted to
 * ensure correct loading of settings and messages.
 */
public class ConfigManager {
    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        Settings.load(plugin);
        Messages.load(plugin);
    }

    public void reloadConfigs() {
        Settings.load(plugin);
        Messages.load(plugin);
    }
}