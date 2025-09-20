package com.wairesd.discordbm.addons.dbmdonatecase.configurators;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class WebhookTriggersConfig {
    public static class Trigger {
        public String type;
        public String caseName;
        public Integer count;
        public String drop;
        public String webhook;
        public String message;
        public Boolean enabled;
        public String period;
        public Double chanceBelow;
        public String player;
    }

    private final List<Trigger> triggers = new ArrayList<>();

    public WebhookTriggersConfig(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        List<Map<?, ?>> list = config.getMapList("webhook_triggers");
        for (Map<?, ?> map : list) {
            Trigger t = new Trigger();
            t.type = Objects.toString(map.get("type"), "");
            t.caseName = Objects.toString(map.get("case"), "*");
            t.count = map.get("count") != null ? Integer.parseInt(map.get("count").toString()) : null;
            t.drop = map.get("drop") != null ? map.get("drop").toString() : null;
            t.webhook = Objects.toString(map.get("webhook"), "");
            t.message = Objects.toString(map.get("message"), "");
            t.enabled = map.get("enabled") == null || Boolean.parseBoolean(map.get("enabled").toString());
            t.period = map.get("period") != null ? map.get("period").toString() : null;
            t.chanceBelow = map.get("chance_below") != null ? Double.parseDouble(map.get("chance_below").toString()) : null;
            t.player = map.get("player") != null ? map.get("player").toString() : null;
            triggers.add(t);
        }
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public List<Trigger> getEnabledTriggers() {
        List<Trigger> enabled = new ArrayList<>();
        for (Trigger t : triggers) if (t.enabled) enabled.add(t);
        return enabled;
    }
} 