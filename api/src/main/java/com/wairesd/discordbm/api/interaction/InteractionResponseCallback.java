package com.wairesd.discordbm.api.interaction;

import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.embed.Embed;

import java.util.List;

/**
 * Interface for responding to Discord interactions
 */
public interface InteractionResponseCallback {
    
    /**
     * Respond to the interaction with a message
     * 
     * @param message The message to send
     * @param ephemeral Whether the message should be ephemeral (only visible to the user who triggered the interaction)
     */
    void respond(String message, boolean ephemeral);
    
    /**
     * Respond to the interaction with an embed
     * 
     * @param embed The embed to send
     * @param ephemeral Whether the message should be ephemeral (only visible to the user who triggered the interaction)
     */
    void respond(Embed embed, boolean ephemeral);
    
    /**
     * Respond to the interaction with an embed and buttons
     * 
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     * @param ephemeral Whether the message should be ephemeral (only visible to the user who triggered the interaction)
     */
    void respond(Embed embed, List<Button> buttons, boolean ephemeral);
    
    /**
     * Update the original message with a new message
     * 
     * @param message The new message
     */
    void updateMessage(String message);
    
    /**
     * Update the original message with a new embed
     * 
     * @param embed The new embed
     */
    void updateMessage(Embed embed);
    
    /**
     * Update the original message with a new embed and buttons
     * 
     * @param embed The new embed
     * @param buttons The new buttons
     */
    void updateMessage(Embed embed, List<Button> buttons);
    
    /**
     * Defer the response (show a loading state)
     * 
     * @param ephemeral Whether the response should be ephemeral
     */
    void deferResponse(boolean ephemeral);
} 