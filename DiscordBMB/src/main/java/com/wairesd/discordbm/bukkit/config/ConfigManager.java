package com.wairesd.discordbm.bukkit.config;

import com.wairesd.discordbm.bukkit.config.configurators.Messages;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import org.bukkit.plugin.java.JavaPlugin;

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