package com.wairesd.discordbm.host.common.config.configurators;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.utils.SecretManager;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class Settings {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private static final String CONFIG_FILE_NAME = "settings.yml";
    private static final String DEFAULT_FORWARDING_SECRET_FILE = "secret.complete.code";

    private static File configFile;
    private static Map<String, Object> config;
    private static SecretManager secretManager;

    public static void init(File dataDir) {
        configFile = new File(dataDir, CONFIG_FILE_NAME);
        loadConfig();
        secretManager = new SecretManager(dataDir.toPath(), getForwardingSecretFile());
    }

    private static void loadConfig() {
        try {
            if (!configFile.exists()) {
                createDefaultConfig();
            }
            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                config = yaml.load(inputStream);
            }
            validateConfig();
        } catch (Exception e) {
            logger.error("Error loading settings.yml: {}", e.getMessage(), e);
        }
    }

    private static void createDefaultConfig() throws IOException {
        configFile.getParentFile().mkdirs();
        try (InputStream inputStream = Settings.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (inputStream != null) {
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Default config loaded from resources to {}", configFile.getPath());
            } else {
                logger.error("{} not found in resources!", CONFIG_FILE_NAME);
                throw new IOException(CONFIG_FILE_NAME + " not found in resources");
            }
        }
    }

    public static void reload() {
        loadConfig();
        secretManager = new SecretManager(configFile.getParentFile().toPath(), getForwardingSecretFile());
        logger.info("settings.yml reloaded successfully");
    }

    private static void validateConfig() {
        if (getBotToken().isEmpty()) {
            logger.warn("Bot-token missing in settings.yml, using default behavior");
        }
    }

    private static boolean getDebugOption(String path, boolean defaultValue) {
        return (boolean) getConfigValue("debug." + path, defaultValue);
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

    public static boolean isDebugCommandReceived() {
        return getDebugOption("debug-command-received", false);
    }

    public static boolean isDebugCommandExecution() {
        return getDebugOption("debug-command-execution", false);
    }

    public static boolean isDebugResolvedMessages() {
        return getDebugOption("debug-resolved-messages", false);
    }

    public static boolean isDebugRequestProcessing() {
        return getDebugOption("debug-request-processing", false);
    }

    public static boolean isDebugCommandNotFound() {
        return getDebugOption("debug-command-not-found", false);
    }

    public static boolean isDebugNettyStart() {
        return getDebugOption("debug-netty-start", false);
    }

    public static boolean isDebugSendMessageAction() {
        return getDebugOption("debug-sendmessage-action", false);
    }

    public static boolean isDebugSendMessageToChannel() {
        return getDebugOption("debug-sendmessage-to-channel", false);
    }

    public static boolean isDebugAuthentication() {
        return getDebugOption("debug-authentication", true);
    }

    public static boolean isDebugErrors() {
        return getDebugOption("debug-errors", true);
    }

    public static String getBotToken() {
        return (String) getConfigValue("Discord.Bot-token", "");
    }

    public static int getNettyPort() {
        return (int) getConfigValue("netty.port", 8080);
    }

    public static String getNettyIp() {
        return (String) getConfigValue("netty.ip", "");
    }


    public static String getForwardingSecretFile() {
        return (String) getConfigValue("forwarding-secret-file", DEFAULT_FORWARDING_SECRET_FILE);
    }

    public static boolean isDefaultEphemeral() {
        return (boolean) getConfigValue("commands.default-ephemeral", false);
    }

    public static long getButtonTimeoutMs() {
        return ((Number) getConfigValue("buttons.timeout-ms", 900_000)).longValue();
    }

    public static boolean isDebugButtonRegister() {
        return getDebugOption("debug-button-register", false);
    }

    public static String getSecretCode() {
        return secretManager != null ? secretManager.getSecretCode() : null;
    }

    public static String getActivityType() {
        return (String) getConfigValue("Discord.activity.type", "playing");
    }

    public static String getActivityMessage() {
        return (String) getConfigValue("Discord.activity.message", "Velocity Server");
    }

    public static boolean isViewConnectedBannedIp() {
        return (boolean) getConfigValue("view_connected_banned_ip", false);
    }

    public static boolean isMySQLEnabled() {
        return (boolean) getConfigValue("mysql.enabled", false);
    }
    public static String getMySQLHost() {
        return (String) getConfigValue("mysql.host", "localhost");
    }
    public static int getMySQLPort() {
        return (int) getConfigValue("mysql.port", 3306);
    }
    public static String getMySQLDatabase() {
        return (String) getConfigValue("mysql.database", "discordbmv");
    }
    public static String getMySQLUsername() {
        return (String) getConfigValue("mysql.username", "root");
    }
    public static String getMySQLPassword() {
        return (String) getConfigValue("mysql.password", "password");
    }
    public static String getMySQLParams() {
        return (String) getConfigValue("mysql.params", "?useSSL=false&serverTimezone=UTC");
    }

    public static String getDatabaseJdbcUrl(String sqlitePath) {
        if (isMySQLEnabled()) {
            return String.format(
                "jdbc:mysql://%s:%d/%s%s",
                getMySQLHost(),
                getMySQLPort(),
                getMySQLDatabase(),
                getMySQLParams()
            ) + String.format("&user=%s&password=%s", getMySQLUsername(), getMySQLPassword());
        } else {
            return "jdbc:sqlite:" + sqlitePath;
        }
    }

    private static Object getConfigValue(String path, Object defaultValue) {
        String[] keys = path.split("\\.");
        Object current = config;
        for (int i = 0; i < keys.length; i++) {
            if (!(current instanceof Map)) {
                return defaultValue;
            }
            Map<?, ?> map = (Map<?, ?>) current;
            current = map.get(keys[i]);
            if (current == null) {
                return defaultValue;
            }
            if (i == keys.length - 1) {
                return current;
            }
        }
        return defaultValue;
    }
}

