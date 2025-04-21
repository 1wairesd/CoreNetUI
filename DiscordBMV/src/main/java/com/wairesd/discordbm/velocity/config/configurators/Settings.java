package com.wairesd.discordbm.velocity.config.configurators;

import com.wairesd.discordbm.velocity.utils.SecretManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class Settings {
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    private static final String CONFIG_FILE_NAME = "settings.yml";
    private static final String DEFAULT_FORWARDING_SECRET_FILE = "secret.complete.code";

    private static Path dataDirectory;
    private static CommentedConfigurationNode config;
    private static SecretManager secretManager;

    public static void init(Path dataDir) {
        dataDirectory = dataDir;
        loadConfig();
        secretManager = new SecretManager(dataDirectory, getForwardingSecretFile());
    }

    private static void loadConfig() {
        CompletableFuture.runAsync(() -> {
            try {
                Path configPath = dataDirectory.resolve(CONFIG_FILE_NAME);
                if (!Files.exists(configPath)) {
                    createDefaultConfig(configPath);
                }

                ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                        .path(configPath)
                        .build();

                config = loader.load();
                validateConfig();
                logger.info("Settings loaded from {}", configPath);
            } catch (Exception e) {
                logger.error("Error loading settings.yml: {}", e.getMessage(), e);
            }
        });
    }

    private static void createDefaultConfig(Path configPath) throws IOException {
        Files.createDirectories(dataDirectory);
        try (var in = Settings.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (in != null) {
                Files.copy(in, configPath);
            } else {
                logger.error("{} not found in resources!", CONFIG_FILE_NAME);
            }
        }
    }

    public static void reload() {
        loadConfig();
        secretManager = new SecretManager(dataDirectory, getForwardingSecretFile());
        Messages.reload();
    }

    private static void validateConfig() {
        if (config == null || !config.node("Discord", "Bot-token").virtual()) {
            logger.warn("Bot-token missing in settings.yml, using default behavior");
        }
    }

    // Debug options
    private static boolean getDebugOption(String path, boolean defaultValue) {
        return config.node("debug", path).getBoolean(defaultValue);
    }

    public static boolean isDebugConnections() {
        return getDebugOption("debug-connections", true);
    }

    public static boolean isDebugClientResponses() {
        return getDebugOption("debug-client-responses", false);
    }

    public static boolean isDebugPluginConnections() {
        return getDebugOption("debug-plugin-connections", false);
    }

    public static boolean isDebugCommandRegistrations() {
        return getDebugOption("debug-command-registrations", false);
    }

    public static boolean isDebugAuthentication() {
        return getDebugOption("debug-authentication", true);
    }

    public static boolean isDebugErrors() {
        return getDebugOption("debug-errors", true);
    }

    // Configuration getters
    public static String getBotToken() {
        return config.node("Discord", "Bot-token").getString();
    }

    public static int getNettyPort() {
        return config.node("netty", "port").getInt(0);
    }

    public static String getForwardingSecretFile() {
        return config.node("forwarding-secret-file").getString(DEFAULT_FORWARDING_SECRET_FILE);
    }

    public static String getSecretCode() {
        return secretManager != null ? secretManager.getSecretCode() : null;
    }

    public static String getActivityType() {
        return config.node("Discord", "activity", "type").getString("playing");
    }

    public static String getActivityMessage() {
        return config.node("Discord", "activity", "message").getString("Velocity Server");
    }

    public static boolean isViewConnectedBannedIp() {
        return config.node("view_connected_banned_ip").getBoolean(false);
    }
}
