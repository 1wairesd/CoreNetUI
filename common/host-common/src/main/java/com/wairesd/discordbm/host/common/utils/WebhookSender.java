package com.wairesd.discordbm.host.common.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookSender {

    /**
     * Sends a POST request to the specified webhook URL with a JSON payload containing the message.
     *
     * @param url the webhook URL
     * @param message the message to send
     */
    public static void sendWebhook(String url, String message) {
        try {
            HttpURLConnection connection = createConnection(url);
            sendPayload(connection, createPayload(message));
            closeConnection(connection);
        } catch (IOException ignored) {
            // Intentionally ignored
        }
    }

    /** Creates and configures the HTTP connection for a POST JSON request. */
    private static HttpURLConnection createConnection(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    /** Constructs the JSON payload from the message. */
    private static String createPayload(String message) {
        return "{\"content\": " + escapeJson(message) + "}";
    }

    /** Writes the payload to the connection's output stream. */
    private static void sendPayload(HttpURLConnection connection, String payload) throws IOException {
        byte[] out = payload.getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(out);
        }
    }

    /** Ensures the input stream of the connection is closed. */
    private static void closeConnection(HttpURLConnection connection) throws IOException {
        connection.getInputStream().close();
    }

    /** Escapes special characters in the message for JSON formatting. */
    private static String escapeJson(String text) {
        if (text == null) return "\"\"";
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
