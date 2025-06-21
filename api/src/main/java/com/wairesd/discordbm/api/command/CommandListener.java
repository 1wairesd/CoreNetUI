package com.wairesd.discordbm.api.command;

/**
 * Interface for listening to command-related events
 */
public interface CommandListener {
    
    /**
     * Called when a command is executed
     * 
     * @param command The command name
     * @param options The command options
     * @param requestId The request ID
     */
    void onCommandExecuted(String command, String[] options, String requestId);
    
    /**
     * Called when a command execution fails
     * 
     * @param command The command name
     * @param options The command options
     * @param requestId The request ID
     * @param error The error message
     */
    void onCommandFailed(String command, String[] options, String requestId, String error);
} 