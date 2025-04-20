package com.wairesd.discordbm.velocity.config.configurators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Messages {
    private static final Logger logger = LoggerFactory.getLogger(Messages.class);
    private static Path dataDirectory;
    private static Map<String, String> messages;

    public static void init(Path dataDir) {
        dataDirectory = dataDir;
        load();
    }

    public static void load() {
        CompletableFuture.runAsync(() -> {
            try {
                Path messagesPath = dataDirectory.resolve("messages.yml");
                if (!Files.exists(messagesPath)) {
                    Files.createDirectories(dataDirectory);
                    try (InputStream in = Messages.class.getClassLoader().getResourceAsStream("messages.yml")) {
                        if (in != null) {
                            Files.copy(in, messagesPath);
                        } else {
                            logger.error("messages.yml not found in resources!");
                            return;
                        }
                    }
                }
                messages = new Yaml().load(Files.newInputStream(messagesPath));
                logger.info("messages.yml loaded successfully");
            } catch (Exception e) {
                logger.error("Error loading messages.yml: {}", e.getMessage(), e);
            }
        });
    }

    public static void reload() {
        load();
    }

    public static String getMessage(String key) {
        return messages != null ? messages.getOrDefault(key, "Message not found.") : "Message not found.";
    }
}