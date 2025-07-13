package com.wairesd.discordbm.api.message;

import com.wairesd.discordbm.api.command.CommandCondition;
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
    void sendResponse(String requestId, Embed embed, List<Button> buttons);

    /**
     * Send a text message with buttons in response to a command
     *
     * @param requestId The request ID of the command
     * @param message The message to send
     * @param buttons The buttons to add to the message
     */
    void sendResponse(String requestId, String message, List<Button> buttons);

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
    void sendForm(String requestId, String message, Form form);

    // --- Response с label ---
    /**
     * Send a text message in response to a command, with label
     */
    void sendResponse(String requestId, String message, String label);
    /**
     * Send an embed in response to a command, with label
     */
    void sendResponse(String requestId, Embed embed, String label);
    /**
     * Send an embed with buttons in response to a command, with label
     */
    void sendResponse(String requestId, Embed embed, List<Button> buttons, String label);
    /**
     * Send a text message with buttons in response to a command, with label
     */
    void sendResponse(String requestId, String message, List<Button> buttons, String label);

    /**
     * Send a text message in response to a command, with conditions
     */
    void sendResponseWithConditions(String requestId, String message, List<CommandCondition> conditions);
    /**
     * Send a text message in response to a command, with conditions and label
     */
    void sendResponseWithConditions(String requestId, String message, List<CommandCondition> conditions, String label);

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

    /**
     * Send an embed as a direct message to a user
     *
     * @param userId The ID of the user to send the message to
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     */
    void sendDirectMessage(String userId, Embed embed, List<Button> buttons);

    /**
     * Send a direct message to a user with buttons
     *
     * @param userId The ID of the user to send the message to
     * @param message The message to send
     * @param buttons The buttons to add to the message
     */
    void sendDirectMessage(String userId, String message, List<Button> buttons);

    /**
     * Send a direct message to a user with requestId and channelId
     *
     * @param userId The ID of the user to send the message to
     * @param message The message to send
     * @param requestId The request ID of the command
     * @param channelId The ID of the channel to send the message to
     */
    void sendDirectMessage(String userId, String message, String requestId, String channelId);

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
     * Send an embed to a channel
     *
     * @param channelId The ID of the channel to send the message to
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     */
    void sendChannelMessage(String channelId, Embed embed, List<Button> buttons);

    /**
     * Send a message to a channel with buttons
     *
     * @param channelId The ID of the channel to send the message to
     * @param message The message to send
     * @param buttons The buttons to add to the message
     */
    void sendChannelMessage(String channelId, String message, List<Button> buttons);

    /**
     * Send a message to a channel, with label
     */
    void sendChannelMessage(String channelId, String message, String label);
    /**
     * Send an embed to a channel, with label
     */
    void sendChannelMessage(String channelId, Embed embed, String label);
    /**
     * Send an embed to a channel with buttons, with label
     */
    void sendChannelMessage(String channelId, Embed embed, List<Button> buttons, String label);
    /**
     * Send a message to a channel with buttons, with label
     */
    void sendChannelMessage(String channelId, String message, List<Button> buttons, String label);

    /**
     * Send a message to a channel, with conditions
     */
    void sendChannelMessageWithConditions(String channelId, String message, List<CommandCondition> conditions);
    /**
     * Send a message to a channel, with conditions and label
     */
    void sendChannelMessageWithConditions(String channelId, String message, List<CommandCondition> conditions, String label);

    /**
     * Send a message with a button that opens a form in response to a command
     * @param requestId The request ID of the command
     * @param message The message to send
     * @param button The button that will open the form
     * @param form The form to open when the button is pressed
     */
    void sendButtonWithForm(String requestId, String message, Button button, Form form);

    /**
     * Edit a previously sent message by label, replacing its text.
     *
     * @param label The label/id of the message to edit
     * @param newMessage The new message text
     */
    void editMessage(String label, String newMessage);

    /**
     * Edit a previously sent message by label, replacing its embed.
     *
     * @param label The label/id of the message to edit
     * @param newEmbed The new embed
     */
    void editMessage(String label, Embed newEmbed);

    /**
     * Edit a previously sent message by label, replacing its embed and buttons.
     *
     * @param label The label/id of the message to edit
     * @param newEmbed The new embed
     * @param newButtons The new buttons
     */
    void editMessage(String label, Embed newEmbed, List<Button> newButtons);

    /**
     * Edit a component (e.g., button) in a previously sent message by label and component id.
     *
     * @param label The label/id of the message
     * @param componentId The id of the component to edit
     * @param newLabel The new label for the component (nullable)
     * @param newStyle The new style for the component (nullable)
     * @param disabled Whether the component should be disabled (nullable)
     */
    void editComponent(String label, String componentId, String newLabel, String newStyle, Boolean disabled);

    /**
     * Delete a previously sent message by label/id.
     *
     * @param label The label/id of the message to delete
     */
    void deleteMessage(String label);
}
