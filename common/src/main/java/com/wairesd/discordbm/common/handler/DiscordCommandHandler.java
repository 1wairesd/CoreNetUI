package com.wairesd.discordbm.common.handler;

public interface DiscordCommandHandler {
    void handleCommand(String command, String[] args, String requestId);
}