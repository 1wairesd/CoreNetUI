package com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class Context {
    private final SlashCommandInteractionEvent event;
    private String messageText = "";
    private final List<Button> buttons = new ArrayList<>();
    private ResponseType responseType = ResponseType.REPLY;
    private String targetChannelId;
    private String targetUserId;
    private String messageIdToEdit;
    private String resolvedMessage;
    private MessageEmbed embed;

    public Context(SlashCommandInteractionEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        this.event = event;
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    public void setEmbed(MessageEmbed embed) {
        this.embed = embed;
    }

    public void setResolvedMessage(String message) { this.resolvedMessage = message; }
    public String getResolvedMessage() { return resolvedMessage; }

    public ResponseType getResponseType() { return responseType; }
    public void setResponseType(ResponseType type) { this.responseType = type; }

    public String getTargetChannelId() { return targetChannelId; }
    public void setTargetChannelId(String id) { this.targetChannelId = id; }

    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String id) { this.targetUserId = id; }

    public String getMessageIdToEdit() { return messageIdToEdit; }
    public void setMessageIdToEdit(String id) { this.messageIdToEdit = id; }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void addButton(Button button) {
        buttons.add(button);
    }
}