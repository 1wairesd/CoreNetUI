package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.commands.commandbuilder.placeolders.user;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Map;

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
        String result = user.replace(template, event);

        for (OptionMapping option : event.getOptions()) {
            String placeholder = "{" + option.getName() + "}";
            result = result.replace(placeholder, option.getAsString());
        }

        return result;
    }

}
