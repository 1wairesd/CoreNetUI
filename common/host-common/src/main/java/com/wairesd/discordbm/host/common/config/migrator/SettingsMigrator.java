package com.wairesd.discordbm.host.common.config.migrator;

import com.wairesd.discordbm.host.common.config.configurators.Settings;

import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsMigrator {
    private static final String CONFIG_META_KEY = "config";
    private static final int LATEST_CONFIG_VERSION = 1;

    public static int getIntConfigValue(String path, int defaultValue) {
        Object value = Settings.getConfigValue(path, defaultValue);
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

    public static boolean migrateConfigIfNeeded() {
        boolean changed = false;

        int version = 0;
        Object meta = Settings.getConfigValue(CONFIG_META_KEY + ".version", null);
        if (meta instanceof Number) version = ((Number) meta).intValue();
        else if (meta instanceof String s) {
            try { version = Integer.parseInt(s.trim()); }
            catch (NumberFormatException ignored) { version = 0; }
        }
        if (version == 0) {
            Object rootVersion = Settings.getConfigValue("version", null);
            if (rootVersion instanceof Number) {
                version = ((Number) rootVersion).intValue();
            } else if (rootVersion instanceof String s) {
                try { version = Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) { version = 0; }
            }
        }

        if (version < LATEST_CONFIG_VERSION) {
            Map<String, Object> root = ensureRootConfig();
            Map<String, Object> newRoot = asStringObjectMap(root.get(Settings.ROOT));

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

            root.put(Settings.ROOT, newRoot);
            changed = true;
        }

        return changed;
    }

    private static Map<String, Object> ensureRootConfig() {
        if (Settings.config == null) {
            Settings.config = new LinkedHashMap<>();
        }
        return Settings.config;
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
