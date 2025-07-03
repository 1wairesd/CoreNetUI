package com.wairesd.discordbm.host.common.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookSender {
    public static void sendWebhook(String url, String message) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String payload = "{\"content\": " + escapeJson(message) + "}";
            byte[] out = payload.getBytes(StandardCharsets.UTF_8);
            connection.getOutputStream().write(out);
            connection.getInputStream().close();
        } catch (Exception e) {
        }
    }

    private static String escapeJson(String text) {
        if (text == null) return "\"\"";
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
} 