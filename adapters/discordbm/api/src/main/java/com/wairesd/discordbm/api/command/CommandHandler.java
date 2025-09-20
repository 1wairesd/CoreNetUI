package com.wairesd.discordbm.api.command;

import java.util.Map;
import com.wairesd.discordbm.api.interaction.InteractionResponseType;

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

    /**
     * Handle Discord modal form submission (modal submit)
     *
     * @param command The command name
     * @param formData Form data as key-value pairs
     * @param requestId Request ID for responding
     */
    default void handleFormSubmit(String command, Map<String, String> formData, String requestId) {}

    /**
     * Defines the Discord interaction response type for this command
     */
    default InteractionResponseType getInteractionResponseType() {
        return InteractionResponseType.AUTO;
    }
}
