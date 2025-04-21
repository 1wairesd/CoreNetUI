package com.wairesd.discordbm.bukkit.config.configurators;

import com.wairesd.discordbm.bukkit.utils.Color;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Manages messages from messages.yml for Bukkit with color translation.
 */
public class Messages {
    private static final Logger logger = LoggerFactory.getLogger(Messages.class);
    private static CommentedConfigurationNode messagesConfig;
    private static YamlConfigurationLoader loader;

    /** Loads messages.yml asynchronously with fallback creation. */
    public static void load(JavaPlugin plugin) {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try (InputStream in = plugin.getResource("messages.yml")) {
                if (in != null) {
                    Files.copy(in, messagesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    logger.warn("messages.yml not found in resources");
                }
            } catch (IOException e) {
                logger.error("Could not save messages.yml: {}", e.getMessage());
            }
        }

        loader = YamlConfigurationLoader.builder()
                .file(messagesFile)
                .build();

        try {
            messagesConfig = loader.load();
        } catch (ConfigurateException e) {
            logger.error("Failed to load messages.yml", e);
        }
    }

    /** Saves the current configuration to messages.yml. */
    public static void save() {
        try {
            loader.save(messagesConfig);
        } catch (ConfigurateException e) {
            logger.error("Failed to save messages.yml", e);
        }
    }

    /**
     * Retrieves a translated message by key.
     * @param key the message key
     * @return the translated message or fallback
     */
    public static String getMessage(String key) {
        String message = messagesConfig.node(key).getString("Message not found.");
        return Color.translate(message);
    }
}
