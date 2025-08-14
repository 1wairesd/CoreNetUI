package com.wairesd.discordbm.client.common.config.converter;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigConverter {
    private static final DumperOptions dumperOptions = new DumperOptions();
    
    static {
        dumperOptions.setIndent(2);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }
    
    public static Yaml createFormattedYaml() {
        return new Yaml(dumperOptions);
    }

    public static Map<String, Object> convert(Map<String, Object> oldConfig, String configType) {
        Map<String, Object> newConfig = new LinkedHashMap<>();
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("version", 1);
        meta.put("type", configType);
        newConfig.put("config", meta);

        Map<String, Object> discordBm = new LinkedHashMap<>();
        if (oldConfig.containsKey("velocity")) {
            Map<String, Object> velocity = (Map<String, Object>) oldConfig.get("velocity");
            Map<String, Object> host = new LinkedHashMap<>();
            host.put("secret", velocity.get("secret"));
            host.put("ip", velocity.get("host"));
            host.put("port", velocity.get("port"));
            discordBm.put("host", host);
        }
        if (oldConfig.containsKey("server")) {
            discordBm.put("server", oldConfig.get("server"));
        }
        if (oldConfig.containsKey("debug")) {
            discordBm.put("debug", oldConfig.get("debug"));
        }
        newConfig.put("DiscordBM", discordBm);
        return newConfig;
    }
}
