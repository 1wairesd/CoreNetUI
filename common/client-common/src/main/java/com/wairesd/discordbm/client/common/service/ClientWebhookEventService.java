package com.wairesd.discordbm.client.common.service;

import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.models.request.WebhookEventRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ClientWebhookEventService {
    private final Platform platform;
    private final Gson gson = new Gson();

    public ClientWebhookEventService(Platform platform) {
        this.platform = platform;
    }

    public void handlePlayerJoinEvent(String playerName, String playerIp) {
        WebhookEventRequest request = new WebhookEventRequest(
            "player_join",
            playerName,
            playerIp,
            null,
            platform.getServerName()
        );
        sendEventToHost(request);
    }

    public void handlePlayerQuitEvent(String playerName, String playerIp, String reason) {
        WebhookEventRequest request = new WebhookEventRequest(
            "player_quit",
            playerName,
            playerIp,
            reason,
            platform.getServerName()
        );
        sendEventToHost(request);
    }

    private void sendEventToHost(WebhookEventRequest request) {
        if (platform.getNettyService() != null && 
            platform.getNettyService().getNettyClient() != null && 
            platform.getNettyService().getNettyClient().isActive()) {
            
            JsonObject wrapper = new JsonObject();
            wrapper.addProperty("type", "webhook_event");
            wrapper.addProperty("data", gson.toJson(request));
            
            String json = gson.toJson(wrapper);
            platform.getNettyService().getNettyClient().send(json);
        }
    }
} 