package com.wairesd.discordbm.api.message;

import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.form.Form;

import java.util.List;

/**
 * Interface for sending messages to Discord
 */
public interface MessageSender {

    // --- Response ---

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
     * Send a form in response to a command
     *
     * @param requestId The request ID of the command
     * @param form The form to send
     */
    void sendForm(String requestId, Form form);

    /**
     * Send a form with a message in response to a command
     *
     * @param requestId The request ID of the command
     * @param message The message to send with the form
     * @param form The form to send
     */
    void sendFormWithMessage(String requestId, String message, Form form);

    // --- Direct Message ---

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

    // --- Channel Message ---

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
