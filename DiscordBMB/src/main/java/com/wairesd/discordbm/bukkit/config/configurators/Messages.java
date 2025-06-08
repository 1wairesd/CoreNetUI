package com.wairesd.discordbm.bukkit.config.configurators;

import com.wairesd.discordbm.common.utils.color.ColorUtils;
import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.bukkit.Bukkit.getLogger;

public class Messages {
    private static final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());
    private static CommentedConfigurationNode messagesConfig;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;

    public static void init(JavaPlugin plugin) {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try (InputStream in = plugin.getResource("messages.yml")) {
                if (in != null) {
                    Files.copy(in, messagesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    pluginLogger.info("Default messages.yml saved to {}", messagesFile.getPath());
                } else {
                    pluginLogger.warn("messages.yml not found in resources");
                }
            } catch (IOException e) {
                pluginLogger.error("Could not save messages.yml: {}", e.getMessage());
            }
        }

        loader = YamlConfigurationLoader.builder()
                .file(messagesFile)
                .build();

        reload(plugin);
    }

    public static void reload(JavaPlugin plugin) {
        try {
            messagesConfig = loader.load();
            pluginLogger.info("messages.yml reloaded successfully");
        } catch (ConfigurateException e) {
            pluginLogger.error("Failed to reload messages.yml", e);
        }
    }

    public static String getMessage(String key) {
        String message = messagesConfig.node(key).getString("Message not found.");
        return ColorUtils.parseString(message);
    }
}
