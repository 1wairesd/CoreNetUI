package com.wairesd.discordbm.client.common.config.configurators;

import com.wairesd.discordbm.client.common.platform.PlatformConfig;
import com.wairesd.discordbm.client.common.config.converter.ConfigConverter;
import com.wairesd.discordbm.common.config.ConfigMetaMigrator;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
                .indent(2)
                .build();

        try {
            settingsConfig = loader.load();

            if (!settingsConfig.node("velocity").virtual()) {
                Map<String, Object> oldConfig = new HashMap<>();
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : settingsConfig.childrenMap().entrySet()) {
                    oldConfig.put(entry.getKey().toString(), entry.getValue().get(Object.class));
                }

                Map<String, Object> newConfig = ConfigConverter.convert(oldConfig, "settings");

                try (java.io.FileWriter writer = new FileWriter(settingsFile)) {
                    Yaml yaml = ConfigConverter.createFormattedYaml();
                    yaml.dump(newConfig, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                java.util.Map<String, Object> cleanedConfig = new java.util.HashMap<>(newConfig);
                cleanedConfig.remove("velocity");
                cleanedConfig.remove("server");
                cleanedConfig.remove("debug");
                
                try (java.io.FileWriter writer = new FileWriter(settingsFile)) {
                    Yaml yaml = ConfigConverter.createFormattedYaml();
                    yaml.dump(cleanedConfig, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    ConfigMetaMigrator.ensureMeta(settingsFile.toPath(), "settings", 1);
                } catch (IOException e) {
                    config.logError("Failed to add config meta", e);
                }

                config.logInfo("Конвертация settings.yml успешно завершена");
            } else if (settingsConfig.node("config", "version").virtual()) {
                settingsConfig.node("config", "version").set(1);
                settingsConfig.node("config", "type").set("settings");
                loader.save(settingsConfig);
            }
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

    public static String getHostHost() {
        CommentedConfigurationNode hostIpNode = settingsConfig.node("DiscordBM", "host", "ip");
        return hostIpNode.getString("127.0.0.1");
    }

    public static int getHostPort() {
        CommentedConfigurationNode hostPortNode = settingsConfig.node("DiscordBM", "host", "port");
        return hostPortNode.getInt(25565);
    }

    public static String getServerName() {
        CommentedConfigurationNode serverNode = settingsConfig.node("DiscordBM", "server");
        return serverNode.getString("ServerName");
    }

    public static String getSecretCode() {
        CommentedConfigurationNode secretNode = settingsConfig.node("DiscordBM", "host", "secret");
        return secretNode.getString("");
    }

    public static boolean isDebugConnections() {
        return settingsConfig.node("DiscordBM", "debug", "debug-connections").getBoolean(false);
    }

    public static boolean isDebugClientResponses() {
        return settingsConfig.node("DiscordBM", "debug", "debug-client-responses").getBoolean(false);
    }

    public static boolean isDebugCommandRegistrations() {
        return settingsConfig.node("DiscordBM", "debug", "debug-command-registrations").getBoolean(false);
    }

    public static boolean isDebugErrors() {
        return settingsConfig.node("DiscordBM", "debug", "debug-errors").getBoolean(true);
    }

    public static boolean isDebugRegisteredServices() {
        return settingsConfig.node("DiscordBM", "debug", "debug-registered-services").getBoolean(false);
    }
}
