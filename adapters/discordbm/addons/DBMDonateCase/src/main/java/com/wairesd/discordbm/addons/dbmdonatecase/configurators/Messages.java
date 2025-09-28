package com.wairesd.discordbm.addons.dbmdonatecase.configurators;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private final Map<String, String> messages = new HashMap<>();
    private final JavaPlugin plugin;

    public Messages(JavaPlugin plugin) {
        this.plugin = plugin;
        try {
            File file = new File(plugin.getDataFolder(), "messages.yml");
            if (!file.exists()) {
                plugin.saveResource("messages.yml", false);
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(new java.io.FileInputStream(file), StandardCharsets.UTF_8)
            );
            for (String key : config.getKeys(false)) {
                messages.put(key, config.getString(key, key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return messages.getOrDefault(key, key);
    }

    public String get(String key, Map<String, String> placeholders) {
        String msg = get(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }
        return msg;
    }

    public void reload() {
        messages.clear();
        try {
            File file = new File(plugin.getDataFolder(), "messages.yml");
            if (!file.exists()) {
                plugin.saveResource("messages.yml", false);
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(new java.io.FileInputStream(file), StandardCharsets.UTF_8)
            );
            for (String key : config.getKeys(false)) {
                messages.put(key, config.getString(key, key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 