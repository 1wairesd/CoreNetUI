package com.wairesd.discordbm.host.common.config.configurators;

import com.wairesd.discordbm.common.config.ConfigMetaMigrator;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Webhooks {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final String WEBHOOKS_FILE_NAME = "webhooks.yml";
    private static Path dataDirectory;
    private static List<Webhook> webhooks = new ArrayList<>();

    public record Webhook(String name, String url, boolean enabled, List<Action> actions, String server) {
        public record Field(String name, String value) {}
        public record Action(String type, String message, String schedule, String title, String description, String color, List<Field> fields, String event) {}
    }

    public static void init(File dataDir) {
        dataDirectory = dataDir.toPath();
        loadWebhooks();
    }

    public static void reload() {
        List<Webhook> reloadedWebhooks = loadWebhooks();
        logger.info("{} reloaded successfully with {} webhooks", WEBHOOKS_FILE_NAME, reloadedWebhooks.size());
    }

    private static synchronized List<Webhook> loadWebhooks() {
        try {
            Path webhooksPath = dataDirectory.resolve(WEBHOOKS_FILE_NAME);
            if (!Files.exists(webhooksPath)) {
                createDefaultWebhooksFile(webhooksPath);
            }
            ConfigMetaMigrator.ensureMeta(webhooksPath, "webhooks", 1);
            List<Webhook> newWebhooks = loadWebhooksFromFile(webhooksPath);
            webhooks = Collections.unmodifiableList(newWebhooks);
            return webhooks;
        } catch (Exception e) {
            logger.error("Error loading {}: {}", WEBHOOKS_FILE_NAME, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private static void createDefaultWebhooksFile(Path webhooksPath) throws IOException {
        Files.createDirectories(dataDirectory);
        try (InputStream in = Webhooks.class.getClassLoader().getResourceAsStream(WEBHOOKS_FILE_NAME)) {
            if (in != null) {
                Files.copy(in, webhooksPath);
            } else {
                logger.error("{} not found in resources!", WEBHOOKS_FILE_NAME);
            }
        }
    }

    private static List<Webhook> loadWebhooksFromFile(Path webhooksPath) throws IOException {
        if (!Files.exists(webhooksPath)) {
            throw new FileNotFoundException("YAML webhooks file not found: " + webhooksPath);
        }
        try (InputStream in = Files.newInputStream(webhooksPath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> loaded = yaml.load(in);
            if (loaded == null || !loaded.containsKey("webhooks")) {
                return Collections.emptyList();
            }
            List<Object> webhooksList = (List<Object>) loaded.get("webhooks");
            List<Webhook> result = new ArrayList<>();
            for (Object webhookObj : webhooksList) {
                Map<String, Object> webhookMap = (Map<String, Object>) webhookObj;
                String name = (String) webhookMap.get("name");
                String url = (String) webhookMap.get("url");
                boolean enabled = Boolean.TRUE.equals(webhookMap.get("enabled"));
                String server = (String) webhookMap.get("server");
                List<Webhook.Action> actions = new ArrayList<>();
                List<Object> actionsList = (List<Object>) webhookMap.get("actions");
                if (actionsList != null) {
                    for (Object actionObj : actionsList) {
                        Map<String, Object> actionMap = (Map<String, Object>) actionObj;
                        String type = (String) actionMap.get("type");
                        String message = (String) actionMap.get("message");
                        String schedule = (String) actionMap.get("schedule");
                        String title = (String) actionMap.get("title");
                        String description = (String) actionMap.get("description");
                        String color = (String) actionMap.get("color");
                        List<Webhook.Field> fields = new ArrayList<>();
                        List<Object> fieldsList = (List<Object>) actionMap.get("fields");
                        if (fieldsList != null) {
                            for (Object fieldObj : fieldsList) {
                                Map<String, Object> fieldMap = (Map<String, Object>) fieldObj;
                                String fieldName = (String) fieldMap.get("name");
                                String fieldValue = (String) fieldMap.get("value");
                                fields.add(new Webhook.Field(fieldName, fieldValue));
                            }
                        }
                        String event = (String) actionMap.get("event");
                        actions.add(new Webhook.Action(type, message, schedule, title, description, color, fields, event));
                    }
                }
                result.add(new Webhook(name, url, enabled, actions, server));
            }
            return result;
        } catch (ClassCastException | IllegalArgumentException e) {
            throw new IOException("Error parsing webhooks.yml: " + e.getMessage(), e);
        }
    }

    public static List<Webhook> getWebhooks() {
        return webhooks;
    }
} 