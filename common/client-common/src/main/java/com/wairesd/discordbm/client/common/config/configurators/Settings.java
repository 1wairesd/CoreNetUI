package com.wairesd.discordbm.client.common.config.configurators;

import com.wairesd.discordbm.client.common.platform.PlatformConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public class Settings {
    private static CommentedConfigurationNode settingsConfig;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;

    public static void init(PlatformConfig config) {
        File settingsFile = new File(config.getDataFolder(), "settings.yml");
        if (!settingsFile.exists()) {
            config.saveResource("settings.yml", false);
        }

        loader = YamlConfigurationLoader.builder()
                .file(settingsFile)
                .build();

        try {
            settingsConfig = loader.load();
        } catch (ConfigurateException e) {
            config.logError("Failed to load settings.yml", e);
        }
    }

    public static void reload(PlatformConfig config) {
        File settingsFile = new File(config.getDataFolder(), "settings.yml");
        if (!settingsFile.exists()) {
            config.saveResource("settings.yml", false);
        }

        try {
            settingsConfig = loader.load();
            config.logInfo("settings.yml reloaded successfully");
        } catch (ConfigurateException e) {
            config.logError("Failed to reload settings.yml", e);
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
