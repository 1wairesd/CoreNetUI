package com.wairesd.discordbm.velocity.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

public class SecretManager {
    private static final Logger logger = LoggerFactory.getLogger(SecretManager.class);
    private final Path secretFilePath;
    private String secretCode;

    public SecretManager(Path dataDirectory, String secretFileName) {
        this.secretFilePath = dataDirectory.resolve(secretFileName);
        loadOrGenerateSecretCode();
    }

    private void loadOrGenerateSecretCode() {
        try {
            if (!Files.exists(secretFilePath)) {
                secretCode = generateSecretCode();
                Files.writeString(secretFilePath, secretCode);
                logger.info("Generated new secret code and saved to {}", secretFilePath.getFileName());
            } else {
                secretCode = Files.readString(secretFilePath).trim();
                logger.info("Loaded secret code from {}", secretFilePath.getFileName());
            }
        } catch (IOException e) {
            logger.error("Error handling secret code file {}: {}", secretFilePath.getFileName(), e.getMessage(), e);
            secretCode = null;
        }
    }

    private String generateSecretCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

        int length = 9 + random.nextInt(4);
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    public String getSecretCode() {
        return secretCode;
    }
}
