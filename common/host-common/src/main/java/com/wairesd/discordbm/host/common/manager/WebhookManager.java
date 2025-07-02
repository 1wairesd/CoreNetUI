package com.wairesd.discordbm.host.common.manager;

import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class WebhookManager {

    public static String handleWebhookToggle(Path dataDirectory, String webhookName, boolean enable) {
        try {
            Path configPath = dataDirectory.resolve("webhooks.yml");
            if (!Files.exists(configPath)) {
                return "webhooks.yml not found!";
            }
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);
            Map<String, Object> loaded;
            try (var in = Files.newInputStream(configPath);
                 var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                loaded = yaml.load(reader);
            }
            if (loaded == null || !loaded.containsKey("webhooks")) {
                return "No webhooks found in config!";
            }
            List<Map<String, Object>> webhooks = (List<Map<String, Object>>) loaded.get("webhooks");
            boolean found = false;
            for (Map<String, Object> webhook : webhooks) {
                if (webhookName.equals(webhook.get("name"))) {
                    webhook.put("enabled", enable);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return "Webhook not found!";
            }
            try (var out = Files.newOutputStream(configPath);
                 var writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                yaml.dump(loaded, writer);
            }
            Webhooks.reload();
            return "Webhook '" + webhookName + "' set to " + enable;
        } catch (Exception e) {
            return "Error updating webhook: " + e.getMessage();
        }
    }
}
