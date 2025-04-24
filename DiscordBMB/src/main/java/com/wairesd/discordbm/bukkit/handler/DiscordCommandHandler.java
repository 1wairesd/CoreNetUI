package com.wairesd.discordbm.bukkit.handler;

/**
 * Interface representing a handler for processing commands received from Discord.
 *
 * Implementing classes should define how commands are handled based on the provided
 * command string, its arguments, and a unique request identifier.
 */
public interface DiscordCommandHandler {
    void handleCommand(String command, String[] args, String requestId);
}