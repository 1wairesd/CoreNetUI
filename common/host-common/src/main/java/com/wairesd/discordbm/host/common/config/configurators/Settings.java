package com.wairesd.discordbm.host.common.config.configurators;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.config.converter.ConfigConverter;
import com.wairesd.discordbm.host.common.utils.SecretManager;
import org.yaml.snakeyaml.Yaml;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Settings {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final String CONFIG_FILE_NAME = "settings.yml";
    private static final String NEW_ROOT = "DiscordBM";
    private static final String CONFIG_META_KEY = "config";
    private static final int LATEST_CONFIG_VERSION = 1;
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
            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                config = yaml.load(inputStream);
            }
            if (config == null) {
                config = new LinkedHashMap<>();
            }
            validateConfig();
        } catch (Exception e) {
            logger.error("Error loading settings.yml: {}", e.getMessage(), e);
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
        Object v = getConfigValue(NEW_ROOT + ".Discord.token", "");
        return String.valueOf(v);
    }

    public static int getNettyPort() {
        return getIntConfigValue(NEW_ROOT + ".netty.port", 8080);
    }

    public static String getNettyIp() {
        Object v = getConfigValue(NEW_ROOT + ".netty.ip", "");
        return String.valueOf(v);
    }

    public static String getForwardingSecretFile() {
        Object v = getConfigValue(NEW_ROOT + ".forwarding-secret-file", DEFAULT_FORWARDING_SECRET_FILE);
        return String.valueOf(v);
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
        return (String) getConfigValue("mysql.database", "DiscordBM");
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

    private static int getIntConfigValue(String path, int defaultValue) {
        Object value = getConfigValue(path, defaultValue);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt(((String) value).trim());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private static boolean migrateConfigIfNeeded() {
        boolean changed = false;

        int version = 0;
        Object meta = getConfigValue(CONFIG_META_KEY + ".version", null);
        if (meta instanceof Number) version = ((Number) meta).intValue();
        else if (meta instanceof String s) {
            try { version = Integer.parseInt(s.trim()); }
            catch (NumberFormatException ignored) { version = 0; }
        }
        if (version == 0) {
            Object rootVersion = getConfigValue("version", null);
            if (rootVersion instanceof Number) {
                version = ((Number) rootVersion).intValue();
            } else if (rootVersion instanceof String s) {
                try { version = Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) { version = 0; }
            }
        }

        if (version < LATEST_CONFIG_VERSION) {
            Map<String, Object> root = ensureRootConfig();
            Map<String, Object> newRoot = asStringObjectMap(root.get(NEW_ROOT));

            Object legacyDiscord = root.get("Discord");
            Map<String, Object> discord = asStringObjectMap(legacyDiscord);
            if (!discord.isEmpty()) {
                Object token = discord.remove("Bot-token");
                if (token != null) discord.put("token", token);
                putUnder(newRoot, "Discord", discord);
                root.remove("Discord");
            }

            Object legacyNetty = root.get("netty");
            Map<String, Object> netty = asStringObjectMap(legacyNetty);
            if (!netty.isEmpty()) {
                putUnder(newRoot, "netty", netty);
                root.remove("netty");
            }

            Object fwd = root.get("forwarding-secret-file");
            if (fwd != null) {
                putUnder(newRoot, "forwarding-secret-file", fwd);
                root.remove("forwarding-secret-file");
            }

            Object legacyDebug = root.get("debug");
            Map<String, Object> debug = asStringObjectMap(legacyDebug);
            if (!debug.isEmpty()) {
                putUnder(newRoot, "debug", debug);
                root.remove("debug");
            }

            Object legacyMysql = root.get("mysql");
            Map<String, Object> mysql = asStringObjectMap(legacyMysql);
            if (!mysql.isEmpty()) {
                putUnder(newRoot, "mysql", mysql);
                root.remove("mysql");
            }

            Object metaObj = root.remove("config");
            Map<String, Object> innerMeta = new LinkedHashMap<>();
            if (metaObj instanceof Map) {
                innerMeta.putAll(asStringObjectMap(metaObj));
            }
            innerMeta.put("version", LATEST_CONFIG_VERSION);
            innerMeta.put("type", "settings");
            root.put("config", innerMeta);

            root.remove("version");

            root.put(NEW_ROOT, newRoot);
            changed = true;
        }

        return changed;
    }

    private static Map<String, Object> ensureRootConfig() {
        if (config == null) {
            config = new LinkedHashMap<>();
        }
        return config;
    }

    private static Map<String, Object> asStringObjectMap(Object value) {
        if (value instanceof Map<?, ?> raw) {
            boolean allStringKeys = true;
            for (Object key : raw.keySet()) {
                if (!(key instanceof String)) {
                    allStringKeys = false;
                    break;
                }
            }
            if (allStringKeys) {
                @SuppressWarnings("unchecked")
                Map<String, Object> casted = (Map<String, Object>) raw;
                return casted;
            }
            Map<String, Object> converted = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : raw.entrySet()) {
                converted.put(String.valueOf(e.getKey()), e.getValue());
            }
            return converted;
        }
        return new LinkedHashMap<>();
    }

    private static void putUnder(Map<String, Object> parent, String key, Object sectionOrValue) {
        if (sectionOrValue instanceof Map) {
            parent.put(key, asStringObjectMap(sectionOrValue));
        } else {
            parent.put(key, sectionOrValue);
        }
    }
}