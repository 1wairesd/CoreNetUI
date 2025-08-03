package com.wairesd.discordbm.host.common.service;

import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks.Webhook;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks.Webhook.Action;
import com.wairesd.discordbm.host.common.utils.WebhookSender;

public class WebhookEventService {
    public static void handlePlayerJoinEvent(String playerName, String playerIp) {
        handlePlayerJoinEvent(playerName, playerIp, "host");
    }

    public static void handlePlayerJoinEvent(String playerName, String playerIp, String serverName) {
        for (Webhook webhook : Webhooks.getWebhooks()) {
            if (!webhook.enabled()) continue;
            
            // Check if webhook is for specific server
            String webhookServer = webhook.server();
            if (webhookServer != null && !webhookServer.equalsIgnoreCase(serverName)) {
                continue; // Skip if webhook is for different server
            }
            
            for (Action action : webhook.actions()) {
                if ("forward_event".equalsIgnoreCase(action.type()) && "player_join".equalsIgnoreCase(action.event())) {
                    String msg = action.message();
                    if (msg != null) {
                        msg = msg.replace("{player}", playerName)
                                .replace("{player_ip}", playerIp)
                                .replace("{server}", serverName);
                        WebhookSender.sendWebhook(webhook.url(), msg);
                    }
                }
            }
        }
    }

    public static void handlePlayerQuitEvent(String playerName, String playerIp, String reason) {
        handlePlayerQuitEvent(playerName, playerIp, reason, "host");
    }

    public static void handlePlayerQuitEvent(String playerName, String playerIp, String reason, String serverName) {
        for (Webhook webhook : Webhooks.getWebhooks()) {
            if (!webhook.enabled()) continue;
            
            // Check if webhook is for specific server
            String webhookServer = webhook.server();
            if (webhookServer != null && !webhookServer.equalsIgnoreCase(serverName)) {
                continue; // Skip if webhook is for different server
            }
            
            for (Action action : webhook.actions()) {
                if ("forward_event".equalsIgnoreCase(action.type()) && "player_quit".equalsIgnoreCase(action.event())) {
                    String msg = action.message();
                    if (msg != null) {
                        msg = msg.replace("{player}", playerName)
                                .replace("{player_ip}", playerIp)
                                .replace("{reason}", reason)
                                .replace("{server}", serverName);
                        WebhookSender.sendWebhook(webhook.url(), msg);
                    }
                }
            }
        }
    }
} 