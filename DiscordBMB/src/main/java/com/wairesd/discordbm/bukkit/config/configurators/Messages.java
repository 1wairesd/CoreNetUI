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
import java.text.MessageFormat;

import static org.bukkit.Bukkit.getLogger;

public class Messages {
    private static final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());
    private static CommentedConfigurationNode messagesConfig;
    private static ConfigurationLoader<CommentedConfigurationNode> loader;

    public static final String DEFAULT_MESSAGE = "Message not found.";

    public static final class Keys {
        public static final String NO_PERMISSION = "no-permission";
        public static final String RELOAD_SUCCESS = "reload-success";
        public static final String COMMAND_UNAVAILABLE = "command-unavailable";
        public static final String HELP_HEADER = "help-header";
        public static final String HELP_RELOAD = "help-reload";
        public static final String HELP_INFO = "help-info";
    }

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

    public static String get(String key, Object... args) {
        String template = getMessage(key);
        return MessageFormat.format(template, args);
    }

    public static String getMessage(String key) {
        return getMessage(key, DEFAULT_MESSAGE);
    }

    public static String getMessage(String key, String defaultValue) {
        String value = messagesConfig.node(key).getString();
        return (value != null && !value.isEmpty()) ? ColorUtils.parseString(value) : defaultValue;
    }
}
