package com.wairesd.discordbm.host.common.scheduler;

import com.wairesd.discordbm.host.common.config.configurators.Webhooks;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebhookScheduler {
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static boolean started = false;

    public static void start() {
        if (started) return;
        started = true;
        List<Webhooks.Webhook> webhooks = Webhooks.getWebhooks();
        for (Webhooks.Webhook webhook : webhooks) {
            if (!webhook.enabled()) continue;
            for (Webhooks.Webhook.Action action : webhook.actions()) {
                if ("send".equalsIgnoreCase(action.type()) && action.schedule() != null) {
                    long period = parseScheduleToSeconds(action.schedule());
                    if (period > 0) {
                        scheduler.scheduleAtFixedRate(() -> sendWebhook(webhook.url(), action.message()), 0, period, TimeUnit.SECONDS);
                    }
                }
                if ("send_embed".equalsIgnoreCase(action.type()) && action.schedule() != null) {
                    long period = parseScheduleToSeconds(action.schedule());
                    if (period > 0) {
                        scheduler.scheduleAtFixedRate(() -> sendEmbedWebhook(webhook.url(), action), 0, period, TimeUnit.SECONDS);
                    }
                }
            }
        }
    }

    private static long parseScheduleToSeconds(String schedule) {
        if (schedule == null) return 0;
        schedule = schedule.trim().toLowerCase();
        if (schedule.startsWith("every ")) {
            String[] parts = schedule.split(" ");
            if (parts.length >= 3) {
                try {
                    long value = Long.parseLong(parts[1]);
                    String unit = parts[2];
                    if (unit.startsWith("second")) return value;
                    if (unit.startsWith("minute")) return value * 60;
                    if (unit.startsWith("hour")) return value * 3600;
                    if (unit.startsWith("day")) return value * 86400;
                    if (unit.startsWith("month")) return value * 30 * 86400;
                    if (unit.startsWith("year")) return value * 365 * 86400;
                } catch (NumberFormatException ignored) {}
            }
        }
        return 0;
    }

    private static void sendWebhook(String url, String message) {
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

    private static void sendEmbedWebhook(String url, Webhooks.Webhook.Action action) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String payload = buildEmbedPayload(action);
            byte[] out = payload.getBytes(StandardCharsets.UTF_8);
            connection.getOutputStream().write(out);
            connection.getInputStream().close();
        } catch (Exception e) {
        }
    }

    private static String buildEmbedPayload(Webhooks.Webhook.Action action) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"embeds\":[{");
        if (action.title() != null) sb.append("\"title\":").append(escapeJson(action.title())).append(",");
        if (action.description() != null) sb.append("\"description\":").append(escapeJson(action.description())).append(",");
        if (action.color() != null) {
            try {
                int colorInt = Integer.decode(action.color());
                sb.append("\"color\":").append(colorInt).append(",");
            } catch (NumberFormatException ignored) {}
        }
        if (action.fields() != null && !action.fields().isEmpty()) {
            sb.append("\"fields\":[");
            for (int i = 0; i < action.fields().size(); i++) {
                Webhooks.Webhook.Field f = action.fields().get(i);
                sb.append("{\"name\":").append(escapeJson(f.name())).append(",\"value\":").append(escapeJson(f.value())).append("}");
                if (i < action.fields().size() - 1) sb.append(",");
            }
            sb.append("],");
        }
        if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        sb.append("}]}");
        return sb.toString();
    }

    private static String escapeJson(String text) {
        if (text == null) return "\"\"";
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public static void shutdown() {
        scheduler.shutdownNow();
        started = false;
        scheduler = Executors.newScheduledThreadPool(2);
    }
}
