package com.wairesd.discordbm.api.command;

import java.util.Map;

/**
 * Interface for handling Discord slash commands
 */
public interface CommandHandler {
    
    /**
     * Handle a Discord slash command
     * 
     * @param command The command name
     * @param options The command options with their values
     * @param requestId The request ID for responding to the command
     */
    void handleCommand(String command, Map<String, String> options, String requestId);
} 