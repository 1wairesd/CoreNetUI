package com.wairesd.discordbm.velocity.command.custom.actions;

import com.wairesd.discordbm.velocity.command.custom.models.CommandAction;
import com.wairesd.discordbm.velocity.command.custom.models.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;

public class SendMessageAction implements CommandAction {
    private final String message;

    public SendMessageAction(Map<String, Object> properties) {
        this.message = (String) properties.getOrDefault("message", "");
        if (this.message.isEmpty()) {
            throw new IllegalArgumentException("Message property is required for SendMessageAction");
        }
    }

    @Override
    public void execute(Context context) {
        if (context == null || context.getEvent() == null) {
            throw new IllegalArgumentException("Context or event cannot be null");
        }
        var event = context.getEvent();
        String formatted = formatMessage(event, message);
        context.setMessageText(formatted);
    }

    private String formatMessage(SlashCommandInteractionEvent event, String message) {
        String result = message.replace("{user}", event.getUser().getName());
        for (var opt : event.getOptions()) {
            String placeholder = "{" + opt.getName() + "}";
            result = result.replace(placeholder, opt.getAsString());
        }
        return result;
    }
}