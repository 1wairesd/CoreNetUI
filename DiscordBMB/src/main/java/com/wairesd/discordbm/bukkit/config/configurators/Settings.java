package com.wairesd.discordbm.bukkit.config.configurators;

import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

import static org.bukkit.Bukkit.getLogger;

public class Settings {
    private static final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());
    private static CommentedConfigurationNode settingsConfig;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;

    public static void init(JavaPlugin plugin) {
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
            pluginLogger.error("Failed to load settings.yml", e);
        }
    }

    public static void reload(JavaPlugin plugin) {
        File settingsFile = new File(plugin.getDataFolder(), "settings.yml");
        if (!settingsFile.exists()) {
            plugin.saveResource("settings.yml", false);
        }

        try {
            settingsConfig = loader.load();
            pluginLogger.info("settings.yml reloaded successfully");
        } catch (ConfigurateException e) {
            pluginLogger.error("Failed to reload settings.yml", e);
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

    public static boolean isDebugConnections() {
        return settingsConfig.node("debug", "debug-connections").getBoolean(true);
    }

    public static boolean isDebugClientResponses() {
        return settingsConfig.node("debug", "debug-client-responses").getBoolean(false);
    }

    public static boolean isDebugCommandRegistrations() {
        return settingsConfig.node("debug", "debug-command-registrations").getBoolean(false);
    }

    public static boolean isDebugErrors() {
        return settingsConfig.node("debug", "debug-errors").getBoolean(true);
    }
}
