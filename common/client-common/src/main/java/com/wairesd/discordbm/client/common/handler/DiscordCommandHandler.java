package com.wairesd.discordbm.client.common.handler;

public interface DiscordCommandHandler {
    void handleCommand(String command, java.util.Map<String, String> options, String requestId);
}