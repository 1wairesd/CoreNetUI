package com.wairesd.discordbm.host.common.service;

import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks.Webhook;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks.Webhook.Action;
import com.wairesd.discordbm.host.common.utils.WebhookSender;

public class WebhookEventService {
    public static void handlePlayerJoinEvent(String playerName, String playerIp) {
        for (Webhook webhook : Webhooks.getWebhooks()) {
            if (!webhook.enabled()) continue;
            for (Action action : webhook.actions()) {
                if ("forward_event".equalsIgnoreCase(action.type()) && "player_join".equalsIgnoreCase(action.event())) {
                    String msg = action.message();
                    if (msg != null) {
                        msg = msg.replace("{player}", playerName).replace("{player_ip}", playerIp);
                        WebhookSender.sendWebhook(webhook.url(), msg);
                    }
                }
            }
        }
    }
} 