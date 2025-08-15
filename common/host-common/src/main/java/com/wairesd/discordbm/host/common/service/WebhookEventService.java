package com.wairesd.discordbm.host.common.service;

import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks.Webhook;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks.Webhook.Action;
import com.wairesd.discordbm.host.common.utils.WebhookSender;

public class WebhookEventService {

    public static void handlePlayerJoinEvent(String playerName, String playerIp) {
        handlePlayerJoinEvent(playerName, playerIp, "host");
    }

    public static void handlePlayerJoinEvent(
            String playerName, String playerIp, String serverName) {
        for (Webhook webhook : Webhooks.getWebhooks()) {
            if (!webhook.enabled() || !isWebhookForServer(webhook, serverName)) continue;
            sendEventMessages(webhook, "player_join", playerName, playerIp, null, serverName);
        }
    }

    public static void handlePlayerQuitEvent(String playerName, String playerIp, String reason) {
        handlePlayerQuitEvent(playerName, playerIp, reason, "host");
    }

    public static void handlePlayerQuitEvent(
            String playerName, String playerIp, String reason, String serverName) {
        for (Webhook webhook : Webhooks.getWebhooks()) {
            if (!webhook.enabled() || !isWebhookForServer(webhook, serverName)) continue;
            sendEventMessages(webhook, "player_quit", playerName, playerIp, reason, serverName);
        }
    }

    /** Checks if the webhook applies to the given server. */
    private static boolean isWebhookForServer(Webhook webhook, String serverName) {
        String webhookServer = webhook.server();
        return webhookServer == null || webhookServer.equalsIgnoreCase(serverName);
    }

    /**
     * Sends webhook messages for a given event type, replacing placeholders with actual values.
     */
    private static void sendEventMessages(
            Webhook webhook, String eventType, String playerName, String playerIp, String reason, String serverName) {
        for (Action action : webhook.actions()) {
            if (!"forward_event".equalsIgnoreCase(action.type())) continue;
            if (!eventType.equalsIgnoreCase(action.event())) continue;

            String msg = action.message();
            if (msg != null) {
                msg = replacePlaceholders(msg, playerName, playerIp, reason, serverName);
                WebhookSender.sendWebhook(webhook.url(), msg);
            }
        }
    }

    /** Replaces placeholders in the message template with actual event values. */
    private static String replacePlaceholders(
            String message, String playerName, String playerIp, String reason, String serverName) {
        message = message.replace("{player}", playerName)
                .replace("{player_ip}", playerIp)
                .replace("{server}", serverName);
        if (reason != null) {
            message = message.replace("{reason}", reason);
        }
        return message;
    }
}
