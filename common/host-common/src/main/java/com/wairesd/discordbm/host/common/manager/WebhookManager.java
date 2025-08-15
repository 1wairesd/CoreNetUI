package com.wairesd.discordbm.host.common.manager;

import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class WebhookManager {

    private static final String WEBHOOKS_CONFIG_FILE = "webhooks.yml";
    private static final String WEBHOOKS_KEY = "webhooks";
    private static final String NAME_KEY = "name";
    private static final String ENABLED_KEY = "enabled";

    public static String handleWebhookToggle(Path dataDirectory, String webhookName, boolean enable) {
        try {
            Path configPath = getWebhookConfigPath(dataDirectory);

            if (!validateConfigFileExists(configPath)) {
                return "webhooks.yml not found!";
            }

            Map<String, Object> configData = loadWebhookConfig(configPath);
            String validationResult = validateConfigData(configData);
            if (validationResult != null) {
                return validationResult;
            }

            List<Map<String, Object>> webhooks = extractWebhooksList(configData);
            if (!updateWebhookStatus(webhooks, webhookName, enable)) {
                return "Webhook not found!";
            }

            saveWebhookConfig(configPath, configData);
            reloadWebhookConfiguration();

            return buildSuccessMessage(webhookName, enable);

        } catch (Exception e) {
            return "Error updating webhook: " + e.getMessage();
        }
    }

    private static Path getWebhookConfigPath(Path dataDirectory) {
        return dataDirectory.resolve(WEBHOOKS_CONFIG_FILE);
    }

    private static boolean validateConfigFileExists(Path configPath) {
        return Files.exists(configPath);
    }

    private static Map<String, Object> loadWebhookConfig(Path configPath) throws IOException {
        Yaml yaml = createYamlInstance();

        try (var inputStream = Files.newInputStream(configPath);
             var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return yaml.load(reader);
        }
    }

    private static Yaml createYamlInstance() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }

    private static String validateConfigData(Map<String, Object> configData) {
        if (configData == null || !configData.containsKey(WEBHOOKS_KEY)) {
            return "No webhooks found in config!";
        }
        return null;
    }

    private static List<Map<String, Object>> extractWebhooksList(Map<String, Object> configData) {
        return (List<Map<String, Object>>) configData.get(WEBHOOKS_KEY);
    }

    private static boolean updateWebhookStatus(List<Map<String, Object>> webhooks, String webhookName, boolean enable) {
        for (Map<String, Object> webhook : webhooks) {
            if (isTargetWebhook(webhook, webhookName)) {
                webhook.put(ENABLED_KEY, enable);
                return true;
            }
        }
        return false;
    }

    private static boolean isTargetWebhook(Map<String, Object> webhook, String webhookName) {
        return webhookName.equals(webhook.get(NAME_KEY));
    }

    private static void saveWebhookConfig(Path configPath, Map<String, Object> configData) throws IOException {
        Yaml yaml = createYamlInstance();

        try (var outputStream = Files.newOutputStream(configPath);
             var writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            yaml.dump(configData, writer);
        }
    }

    private static void reloadWebhookConfiguration() {
        Webhooks.reload();
    }

    private static String buildSuccessMessage(String webhookName, boolean enable) {
        return "Webhook '" + webhookName + "' set to " + enable;
    }
}