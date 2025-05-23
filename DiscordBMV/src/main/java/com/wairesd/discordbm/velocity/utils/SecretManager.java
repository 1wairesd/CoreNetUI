package com.wairesd.discordbm.velocity.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

public class SecretManager {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    private final Path secretFilePath;
    private final String secretCode;

    public SecretManager(Path dataDirectory, String secretFileName) {
        this.secretFilePath = dataDirectory.resolve(secretFileName);
        this.secretCode = loadOrGenerateSecretCode();
    }

    private String loadOrGenerateSecretCode() {
        try {
            if (Files.exists(secretFilePath)) {
                String loadedCode = Files.readString(secretFilePath).trim();
                return loadedCode;
            } else {
                String generatedCode = generateSecretCode();
                Files.writeString(secretFilePath, generatedCode);
                logger.info("Generated new secret code and saved to {}", secretFilePath.getFileName());
                return generatedCode;
            }
        } catch (IOException e) {
            logger.error("Error handling secret code file {}: {}", secretFilePath.getFileName(), e.getMessage(), e);
            return null;
        }
    }

    private String generateSecretCode() {
        int length = 16 + random.nextInt(10);
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }

    public String getSecretCode() {
        return secretCode;
    }
}
