package com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    private final SlashCommandInteractionEvent event;
    private String messageText = "";
    private ResponseType responseType = ResponseType.REPLY;
    private String targetChannelId;
    private String targetUserId;
    private String messageIdToEdit;
    private String resolvedMessage;
    private MessageEmbed embed;
    private final List<ActionRow> actionRows = new ArrayList<>();
    private final Map<String, String> messageLabels = new HashMap<>();
    private String expectedMessageLabel;

    public Context(SlashCommandInteractionEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        this.event = event;
    }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public String getTargetChannelId() {
        return targetChannelId;
    }

    public void setTargetChannelId(String targetChannelId) {
        this.targetChannelId = targetChannelId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getMessageIdToEdit() {
        return messageIdToEdit;
    }

    public void setMessageIdToEdit(String messageIdToEdit) {
        this.messageIdToEdit = messageIdToEdit;
    }

    public String getResolvedMessage() {
        return resolvedMessage;
    }

    public void setResolvedMessage(String resolvedMessage) {
        this.resolvedMessage = resolvedMessage;
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    public void setEmbed(MessageEmbed embed) {
        this.embed = embed;
    }

    public List<ActionRow> getActionRows() {
        return actionRows;
    }

    public void setActionRows(List<ActionRow> actionRows) {
        this.actionRows.clear();
        this.actionRows.addAll(actionRows);
    }

    public void addActionRow(ActionRow actionRow) {
        this.actionRows.add(actionRow);
    }

    public Map<String, String> getMessageLabels() {
        return messageLabels;
    }

    public void setExpectedMessageLabel(String label) {
        this.expectedMessageLabel = label;
    }

    public String getExpectedMessageLabel() {
        return expectedMessageLabel;
    }

    public void addMessageLabel(String label, String messageId) {
        this.messageLabels.put(label, messageId);
    }

    public String getMessageIdByLabel(String label) {
        return messageLabels.get(label);
    }
}