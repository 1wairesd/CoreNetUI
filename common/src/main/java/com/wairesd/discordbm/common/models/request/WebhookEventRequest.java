package com.wairesd.discordbm.common.models.request;

/**
 * Request model for webhook events sent from client to host
 */
public class WebhookEventRequest {
    private final String type;
    private final String playerName;
    private final String playerIp;
    private final String reason;
    private final String serverName;

    public WebhookEventRequest(String type, String playerName, String playerIp, String reason, String serverName) {
        this.type = type;
        this.playerName = playerName;
        this.playerIp = playerIp;
        this.reason = reason;
        this.serverName = serverName;
    }

    public String getType() {
        return type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public String getReason() {
        return reason;
    }

    public String getServerName() {
        return serverName;
    }
} 