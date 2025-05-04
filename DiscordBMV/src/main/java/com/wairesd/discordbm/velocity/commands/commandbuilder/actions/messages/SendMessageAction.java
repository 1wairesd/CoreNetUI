package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.ResponseType;
import com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders.PlaceholdersUser;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SendMessageAction implements CommandAction {
    private static final Logger logger = LoggerFactory.getLogger(SendMessageAction.class);
    private static final String DEFAULT_MESSAGE = "";
    private final String messageTemplate;
    private final ResponseType responseType;
    private final String targetId;

    public SendMessageAction(Map<String, Object> properties) {
        validateProperties(properties);
        this.messageTemplate = (String) properties.getOrDefault("message", DEFAULT_MESSAGE);

        this.responseType = ResponseType.valueOf(
                ((String) properties.getOrDefault("response_type", "REPLY")).toUpperCase()
        );
        this.targetId = (String) properties.get("target_id");

        if (responseType == ResponseType.SPECIFIC_CHANNEL || responseType == ResponseType.EDIT_MESSAGE) {
            if (targetId == null || targetId.isEmpty()) {
                throw new IllegalArgumentException("target_id is required for " + responseType);
            }
        }
    }

    private void validateProperties(Map<String, Object> properties) {
        String message = (String) properties.get("message");
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message property is required for SendMessageAction");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        return CompletableFuture.runAsync(() -> {
            validateContext(context);
            SlashCommandInteractionEvent event = context.getEvent();
            String formattedTargetId = formatMessage(event, this.targetId, context);
            String formattedMessage = formatMessage(event, messageTemplate, context);
            context.setMessageText(formattedMessage);

            context.setResponseType(responseType);
            switch (responseType) {
                case SPECIFIC_CHANNEL:
                    context.setTargetChannelId(formattedTargetId);
                    break;
                case DIRECT_MESSAGE:
                    String userId = formattedTargetId;
                    if (userId != null && !userId.isEmpty()) {
                        context.setTargetUserId(userId);
                    } else {
                        logger.warn("Target user ID is null or empty, unable to send direct message.");
                    }
                    break;
                case EDIT_MESSAGE:
                    context.setMessageIdToEdit(targetId);
                    break;
                case REPLY:
                    break;
                default:
                    logger.warn("Unknown Response Type: {}", responseType);
                    break;
            }
        });
    }

    private void validateContext(Context context) {
        if (context == null || context.getEvent() == null) {
            throw new NullPointerException("Context or event cannot be null");
        }
    }

    private String formatMessage(SlashCommandInteractionEvent event, String template, Context context) {
        String result = PlaceholdersUser.replace(template != null ? template : "", event, context);

        if (Settings.isDebugSendMessageAction()) {
            logger.info("Resolved message in SendMessageAction: {}", context.getResolvedMessage());
            logger.info("Formatted message in SendMessageAction: {}", result);
        }

        for (OptionMapping option : event.getOptions()) {
            String placeholder = "{" + option.getName() + "}";
            result = result.replace(placeholder, option.getAsString());
        }
        return result;
    }

}