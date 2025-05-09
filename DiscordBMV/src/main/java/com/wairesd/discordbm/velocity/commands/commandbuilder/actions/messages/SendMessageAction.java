package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.ResponseType;
import com.wairesd.discordbm.velocity.commands.commandbuilder.utils.ContextUtils;
import com.wairesd.discordbm.velocity.commands.commandbuilder.utils.EmbedFactoryUtils;
import com.wairesd.discordbm.velocity.commands.commandbuilder.utils.MessageFormatterUtils;
import com.wairesd.discordbm.velocity.commands.commandbuilder.utils.TargetIDResolverUtils;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
    private final Map<String, Object> embedProperties;
    private final String label;

    public SendMessageAction(Map<String, Object> properties) {
        validateProperties(properties);
        this.messageTemplate = (String) properties.getOrDefault("message", DEFAULT_MESSAGE);
        this.embedProperties = (Map<String, Object>) properties.get("embed");
        this.responseType = ResponseType.valueOf(
                ((String) properties.getOrDefault("response_type", "REPLY")).toUpperCase()
        );
        this.targetId = (String) properties.get("target_id");
        this.label = (String) properties.get("label");

        if ((responseType == ResponseType.SPECIFIC_CHANNEL || responseType == ResponseType.EDIT_MESSAGE)
                && (targetId == null || targetId.isEmpty())) {
            throw new IllegalArgumentException("target_id is required for " + responseType);
        }
    }

    private void validateProperties(Map<String, Object> properties) {
        boolean hasMessage = properties.containsKey("message") && !((String) properties.get("message")).isEmpty();
        boolean

                hasEmbed = properties.containsKey("embed");
        if (!hasMessage && !hasEmbed) {
            throw new IllegalArgumentException("Message or embed is required for SendMessageAction");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        CompletableFuture<Void> resultFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                ContextUtils.validate(context);
                SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();
                String formattedTargetId = TargetIDResolverUtils.resolve(event, this.targetId, context);
                String formattedMessage = MessageFormatterUtils.format(messageTemplate, event, context, Settings.isDebugSendMessageAction());
                context.setMessageText(formattedMessage);

                if (embedProperties != null) {
                    MessageEmbed embed = EmbedFactoryUtils.create(embedProperties, event, context);
                    context.setEmbed(embed);
                }

                if (this.label != null) {
                    context.setExpectedMessageLabel(this.label);
                }

                context.setResponseType(responseType);
                switch (responseType) {
                    case SPECIFIC_CHANNEL:
                        context.setTargetChannelId(formattedTargetId);
                        resultFuture.complete(null);
                        break;
                    case DIRECT_MESSAGE:
                        if (formattedTargetId != null && !formattedTargetId.isEmpty()) {
                            context.setTargetUserId(formattedTargetId);
                        }
                        resultFuture.complete(null);
                        break;
                    case EDIT_MESSAGE:
                        context.setMessageIdToEdit(formattedTargetId);
                        resultFuture.complete(null);
                        break;
                    case REPLY:
                        resultFuture.complete(null);
                        break;
                    default:
                        resultFuture.complete(null);
                }
            } catch (Throwable t) {
                resultFuture.completeExceptionally(t);
            }
        });

        return resultFuture;
    }
}