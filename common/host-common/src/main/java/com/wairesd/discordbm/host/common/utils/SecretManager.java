package com.wairesd.discordbm.host.common.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

public class SecretManager {
    private static final PluginLogger logger =
            new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final SecureRandom random = new SecureRandom();

    private final Path secretFilePath;
    @Getter private final String secretCode;

    public SecretManager(Path dataDirectory, String secretFileName) {
        this.secretFilePath = dataDirectory.resolve(secretFileName);
        this.secretCode = loadOrGenerateSecretCode();
    }

    /**
     * Loads the secret code from file if it exists, otherwise generates a new secret code, saves it,
     * and returns it.
     *
     * @return the secret code as a Base64 string
     */
    private String loadOrGenerateSecretCode() {
        try {
            if (Files.exists(secretFilePath)) {
                return loadSecretFromFile();
            } else {
                return generateAndSaveSecret();
            }
        } catch (IOException e) {
            logger.error(
                    "Error handling secret code file {}: {}", secretFilePath.getFileName(), e.getMessage(), e);
            return null;
        }
    }

    /** Reads and trims the secret code from the file. */
    private String loadSecretFromFile() throws IOException {
        return Files.readString(secretFilePath).trim();
    }

    /** Generates a new secret code, encodes it in Base64, writes it to file, and returns it. */
    private String generateAndSaveSecret() throws IOException {
        String rawSecret = generateRawSecretCode();
        String base64Secret = encodeBase64(rawSecret);
        saveSecretToFile(base64Secret);
        return base64Secret;
    }

    /** Encodes the given string into Base64 using UTF-8. */
    private String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /** Writes the secret string to the secret file and logs the action. */
    private void saveSecretToFile(String secret) throws IOException {
        Files.writeString(secretFilePath, secret);
        logger.info("Generated new Base64 secret code and saved to {}", secretFilePath.getFileName());
    }

    /** Generates a raw secret string with random valid Unicode characters. */
    private String generateRawSecretCode() {
        int length = 15 + random.nextInt(16);
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.appendCodePoint(generateValidCodePoint());
        }

        return sb.toString();
    }

    /** Generates a single valid Unicode code point that is defined and not a control character. */
    private int generateValidCodePoint() {
        int codePoint;
        do {
            codePoint = random.nextInt(Character.MAX_CODE_POINT + 1);
        } while (!Character.isDefined(codePoint) || Character.isISOControl(codePoint));
        return codePoint;
    }
}
