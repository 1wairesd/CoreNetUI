package com.wairesd.discordbm.common.models.register;

public class ClientRegisterMessage {
    private final String type = "client_register";
    private final String serverName;
    private final String secret;

    public ClientRegisterMessage(String serverName, String secret) {
        this.serverName = serverName;
        this.secret = secret;
    }

    public String getType() {
        return type;
    }

    public String getServerName() {
        return serverName;
    }

    public String getSecret() {
        return secret;
    }
}