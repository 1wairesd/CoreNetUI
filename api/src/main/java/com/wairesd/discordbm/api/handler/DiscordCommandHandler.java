package com.wairesd.discordbm.api.handler;

public interface DiscordCommandHandler {
    void handleCommand(String command, String[] args, String requestId);
}