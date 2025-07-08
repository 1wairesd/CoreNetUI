package com.wairesd.discordbm.client.common.config.configurators;

import com.wairesd.discordbm.client.common.platform.PlatformConfig;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
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

public class Messages {
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

    public static void init(PlatformConfig config) {
        File messagesFile = new File(config.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try (InputStream in = config.getResource("messages.yml")) {
                if (in != null) {
                    Files.copy(in, messagesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    config.logInfo("Default messages.yml saved to " + messagesFile.getPath());
                } else {
                    config.logWarning("messages.yml not found in resources");
                }
            } catch (IOException e) {
                config.logError("Could not save messages.yml: " + e.getMessage(), e);
            }
        }

        loader = YamlConfigurationLoader.builder()
                .file(messagesFile)
                .build();

        reload(config);
    }

    public static void reload(PlatformConfig config) {
        try {
            messagesConfig = loader.load();
            config.logInfo("messages.yml reloaded successfully");
        } catch (ConfigurateException e) {
            config.logError("Failed to reload messages.yml", e);
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
