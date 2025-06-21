package com.wairesd.discordbm.api.message;

import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.embed.Embed;

import java.util.List;

/**
 * Interface for sending messages to Discord
 */
public interface MessageSender {
    
    /**
     * Send a text message in response to a command
     * 
     * @param requestId The request ID of the command
     * @param message The message to send
     */
    void sendResponse(String requestId, String message);
    
    /**
     * Send an embed in response to a command
     * 
     * @param requestId The request ID of the command
     * @param embed The embed to send
     */
    void sendResponse(String requestId, Embed embed);
    
    /**
     * Send an embed with buttons in response to a command
     * 
     * @param requestId The request ID of the command
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     */
    void sendResponseWithButtons(String requestId, Embed embed, List<Button> buttons);
    
    /**
     * Send a direct message to a user
     * 
     * @param userId The ID of the user to send the message to
     * @param message The message to send
     */
    void sendDirectMessage(String userId, String message);
    
    /**
     * Send an embed as a direct message to a user
     * 
     * @param userId The ID of the user to send the message to
     * @param embed The embed to send
     */
    void sendDirectMessage(String userId, Embed embed);
    
    /**
     * Send a message to a channel
     * 
     * @param channelId The ID of the channel to send the message to
     * @param message The message to send
     */
    void sendChannelMessage(String channelId, String message);
    
    /**
     * Send an embed to a channel
     * 
     * @param channelId The ID of the channel to send the message to
     * @param embed The embed to send
     */
    void sendChannelMessage(String channelId, Embed embed);
    
    /**
     * Send an embed with buttons to a channel
     * 
     * @param channelId The ID of the channel to send the message to
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     */
    void sendChannelMessageWithButtons(String channelId, Embed embed, List<Button> buttons);
} 