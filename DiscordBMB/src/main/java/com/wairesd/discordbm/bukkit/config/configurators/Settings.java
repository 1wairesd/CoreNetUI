package com.wairesd.discordbm.bukkit.config.configurators;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

/**
 * Manages settings from settings.yml for Bukkit.
 */
public class Settings {
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    private static CommentedConfigurationNode settingsConfig;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;

    /** Loads settings.yml asynchronously with default saving. */
    public static void load(JavaPlugin plugin) {
        File settingsFile = new File(plugin.getDataFolder(), "settings.yml");
        if (!settingsFile.exists()) {
            plugin.saveResource("settings.yml", false);
        }

        loader = YamlConfigurationLoader.builder()
                .file(settingsFile)
                .build();

        try {
            settingsConfig = loader.load();
        } catch (ConfigurateException e) {
            logger.error("Failed to load settings.yml", e);
        }
    }

    /** Saves the current configuration to settings.yml. */
    public static void save() {
        try {
            loader.save(settingsConfig);
        } catch (ConfigurateException e) {
            logger.error("Failed to save settings.yml", e);
        }
    }

    public static String getVelocityHost() {
        return settingsConfig.node("velocity", "host").getString("127.0.0.1");
    }

    public static int getVelocityPort() {
        return settingsConfig.node("velocity", "port").getInt(8080);
    }

    public static String getServerName() {
        return settingsConfig.node("server").getString("ServerName");
    }

    public static String getSecretCode() {
        return settingsConfig.node("velocity", "secret").getString("");
    }

    // Debug settings from the query
    public static boolean isDebugConnections() {
        return settingsConfig.node("debug", "debug-connections").getBoolean(true);
    }

    public static boolean isDebugClientResponses() {
        return settingsConfig.node("debug", "debug-client-responses").getBoolean(false);
    }

    public static boolean isDebugCommandRegistrations() {
        return settingsConfig.node("debug", "debug-command-registrations").getBoolean(false);
    }

    public static boolean isDebugAuthentication() {
        return settingsConfig.node("debug", "debug-authentication").getBoolean(true);
    }

    public static boolean isDebugErrors() {
        return settingsConfig.node("debug", "debug-errors").getBoolean(true);
    }
}
