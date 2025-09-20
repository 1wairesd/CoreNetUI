package com.wairesd.discordbm.api.message;

import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.modal.Modal;

import java.util.List;

/**
 * Abstract class for sending messages to Discord
 */
public abstract class MessageSender {

    // --- Response ---

    /**
     * Send a text message in response to a command
     *
     * @param requestId The request ID of the command
     * @param message The message to send
     */
    public abstract void sendResponse(String requestId, String message);

    /**
     * Send an embed in response to a command
     *
     * @param requestId The request ID of the command
     * @param embed The embed to send
     */
    public abstract void sendResponse(String requestId, Embed embed);

    /**
     * Send an embed with buttons in response to a command
     *
     * @param requestId The request ID of the command
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     */
    public abstract void sendResponse(String requestId, Embed embed, List<Button> buttons);

    /**
     * Send a text message with buttons in response to a command
     *
     * @param requestId The request ID of the command
     * @param message The message to send
     * @param buttons The buttons to add to the message
     */
    public abstract void sendResponse(String requestId, String message, List<Button> buttons);

    /**
     * Send a modal in response to a command
     *
     * @param requestId The request ID of the command
     * @param modal The modal to send
     */
    public abstract void sendModal(String requestId, Modal modal);

    /**
     * Send a modal with a message in response to a command
     *
     * @param requestId The request ID of the command
     * @param message The message to send with the modal
     * @param modal The modal to send
     */
    public abstract void sendModal(String requestId, String message, Modal modal);

    // --- Response с label ---
    /**
     * Send a text message in response to a command, with label
     */
    public abstract void sendResponse(String requestId, String message, String label);
    /**
     * Send an embed in response to a command, with label
     */
    public abstract void sendResponse(String requestId, Embed embed, String label);
    /**
     * Send an embed with buttons in response to a command, with label
     */
    public abstract void sendResponse(String requestId, Embed embed, List<Button> buttons, String label);
    /**
     * Send a text message with buttons in response to a command, with label
     */
    public abstract void sendResponse(String requestId, String message, List<Button> buttons, String label);

    // --- Direct Message ---

    /**
     * Send a direct message to a user
     *
     * @param userId The ID of the user to send the message to
     * @param message The message to send
     */
    public abstract void sendDirectMessage(String userId, String message);

    /**
     * Send an embed as a direct message to a user
     *
     * @param userId The ID of the user to send the message to
     * @param embed The embed to send
     */
    public abstract void sendDirectMessage(String userId, Embed embed);

    /**
     * Send an embed as a direct message to a user
     *
     * @param userId The ID of the user to send the message to
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     */
    public abstract void sendDirectMessage(String userId, Embed embed, List<Button> buttons);

    /**
     * Send a direct message to a user with buttons
     *
     * @param userId The ID of the user to send the message to
     * @param message The message to send
     * @param buttons The buttons to add to the message
     */
    public abstract void sendDirectMessage(String userId, String message, List<Button> buttons);

    /**
     * Send a direct message to a user with requestId and channelId
     *
     * @param userId The ID of the user to send the message to
     * @param message The message to send
     * @param requestId The request ID of the command
     * @param channelId The ID of the channel to send the message to
     */
    public abstract void sendDirectMessage(String userId, String message, String requestId, String channelId);

    // --- Channel Message ---

    /**
     * Send a message to a channel
     *
     * @param channelId The ID of the channel to send the message to
     * @param message The message to send
     */
    public abstract void sendChannelMessage(String channelId, String message);

    /**
     * Send an embed to a channel
     *
     * @param channelId The ID of the channel to send the message to
     * @param embed The embed to send
     */
    public abstract void sendChannelMessage(String channelId, Embed embed);

    /**
     * Send an embed to a channel
     *
     * @param channelId The ID of the channel to send the message to
     * @param embed The embed to send
     * @param buttons The buttons to add to the message
     */
    public abstract void sendChannelMessage(String channelId, Embed embed, List<Button> buttons);

    /**
     * Send a message to a channel with buttons
     *
     * @param channelId The ID of the channel to send the message to
     * @param message The message to send
     * @param buttons The buttons to add to the message
     */
    public abstract void sendChannelMessage(String channelId, String message, List<Button> buttons);

    /**
     * Send a message to a channel, with label
     */
    public abstract void sendChannelMessage(String channelId, String message, String label);
    /**
     * Send an embed to a channel, with label
     */
    public abstract void sendChannelMessage(String channelId, Embed embed, String label);
    /**
     * Send an embed to a channel with buttons, with label
     */
    public abstract void sendChannelMessage(String channelId, Embed embed, List<Button> buttons, String label);
    /**
     * Send a message to a channel with buttons, with label
     */
    public abstract void sendChannelMessage(String channelId, String message, List<Button> buttons, String label);

    /**
     * Send a message with a button that opens a modal in response to a command
     * @param requestId The request ID of the command
     * @param message The message to send
     * @param button The button that will open the modal
     * @param modal The modal to open when the button is pressed
     */
    public abstract void sendButtonWithModal(String requestId, String message, Button button, Modal modal);

    /**
     * Send a random reply (random message from the list) in response to a command
     * @param requestId The request ID of the command
     * @param messages The list of possible messages
     */
    public abstract void sendRandomReply(String requestId, List<String> messages);

    /**
     * Edit a previously sent message by label, replacing its text.
     *
     * @param label The label/id of the message to edit
     * @param newMessage The new message text
     */
    public abstract void editMessage(String label, String newMessage);

    /**
     * Edit a previously sent message by label, replacing its embed.
     *
     * @param label The label/id of the message to edit
     * @param newEmbed The new embed
     */
    public abstract void editMessage(String label, Embed newEmbed);

    /**
     * Edit a previously sent message by label, replacing its embed and buttons.
     *
     * @param label The label/id of the message to edit
     * @param newEmbed The new embed
     * @param newButtons The new buttons
     */
    public abstract void editMessage(String label, Embed newEmbed, List<Button> newButtons);

    /**
     * Edit a component (e.g., button) in a previously sent message by label and component id.
     *
     * @param label The label/id of the message
     * @param componentId The id of the component to edit
     * @param newLabel The new label for the component (nullable)
     * @param newStyle The new style for the component (nullable)
     * @param disabled Whether the component should be disabled (nullable)
     */
    public abstract void editComponent(String label, String componentId, String newLabel, String newStyle, Boolean disabled);

    /**
     * Delete a previously sent message by label/id.
     *
     * @param label The label/id of the message to delete
     * @param deleteAll If true, delete all messages with this label; if false, only the last one
     */
    public abstract void deleteMessage(String label, boolean deleteAll);

    /**
     * Delete all messages with the given label (default behavior).
     * @param label The label/id of the message to delete
     */
    public void deleteMessage(String label) {
        deleteMessage(label, true);
    }

    /**
     * Send a reply to a specific message (REPLY_TO_MESSAGE)
     * @param requestId The request ID of the command
     * @param message The message to send
     * @param replyMessageId The ID of the message to reply to
     * @param mentionAuthor Whether to mention the author of the original message
     */
    public abstract void sendReplyToMessage(String requestId, String message, String replyMessageId, boolean mentionAuthor);

    /**
     * Отправить сообщение в Discord через webhook по имени из webhooks.yml
     * @param webhookName имя вебхука из webhooks.yml
     * @param message сообщение для отправки
     * @throws IllegalArgumentException если вебхук не найден
     */
    public abstract void sendWebhook(String webhookName, String message);
}
