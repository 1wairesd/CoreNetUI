package com.wairesd.discordbm.velocity.commands.custom.actions;

import com.wairesd.discordbm.velocity.commands.custom.models.CommandAction;
import com.wairesd.discordbm.velocity.commands.custom.models.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Map;
import java.util.Objects;

public class SendMessageAction implements CommandAction {
    private static final String DEFAULT_MESSAGE = "";
    private final String messageTemplate;

    public SendMessageAction(Map<String, Object> properties) {
        validateProperties(properties);
        this.messageTemplate = (String) properties.getOrDefault("message", DEFAULT_MESSAGE);
    }

    private void validateProperties(Map<String, Object> properties) {
        String message = (String) properties.get("message");
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message property is required for SendMessageAction");
        }
    }

    @Override
    public void execute(Context context) {
        validateContext(context);
        SlashCommandInteractionEvent event = context.getEvent();
        String formattedMessage = formatMessage(event, messageTemplate);
        context.setMessageText(formattedMessage);
    }

    private void validateContext(Context context) {
        if (context == null || context.getEvent() == null) {
            throw new NullPointerException("Context or event cannot be null");
        }
    }

    private String formatMessage(SlashCommandInteractionEvent event, String template) {
        Objects.requireNonNull(event, "Event cannot be null");
        Objects.requireNonNull(template, "Message template cannot be null");

        String result = template.replace("{user}", event.getUser().getAsTag());

        for (OptionMapping option : event.getOptions()) {
            String placeholder = "{" + option.getName() + "}";
            result = result.replace(placeholder, option.getAsString());
        }

        return result;
    }
}
