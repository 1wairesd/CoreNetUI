package com.wairesd.discordbm.client.common.handler;

public interface DiscordCommandHandler {
    void handleCommand(String command, String[] args, String requestId);
}