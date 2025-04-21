package com.wairesd.discordbm.velocity.commands.custom.models;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class Context {
    private final SlashCommandInteractionEvent event;
    private String messageText = "";
    private final List<Button> buttons = new ArrayList<>();

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

    public List<Button> getButtons() {
        return buttons;
    }

    public void addButton(Button button) {
        buttons.add(button);
    }
}