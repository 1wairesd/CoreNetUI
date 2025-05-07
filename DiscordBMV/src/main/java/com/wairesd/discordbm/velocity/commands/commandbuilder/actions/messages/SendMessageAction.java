package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages;

import com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders.message_id;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.ResponseType;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders.PlaceholdersUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
                validateContext(context);
                SlashCommandInteractionEvent event = context.getEvent();
                String formattedTargetId = resolveTargetId(event, this.targetId, context);
                String formattedMessage = formatMessage(event, messageTemplate, context);
                context.setMessageText(formattedMessage);

                if (embedProperties != null) {
                    MessageEmbed embed = createEmbed(embedProperties, event, context);
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

    private String resolveTargetId(SlashCommandInteractionEvent event, String targetId, Context context) {
        return message_id.resolveMessageId(targetId, context);
    }

    private void validateContext(Context context) {
        if (context == null || context.getEvent() == null) {
            throw new NullPointerException("Context or event cannot be null");
        }
    }

    private MessageEmbed createEmbed(Map<String, Object> embedMap, SlashCommandInteractionEvent event, Context context) {
        EmbedBuilder builder = new EmbedBuilder();

        if (embedMap.containsKey("title")) {
            String title = formatMessage(event, (String) embedMap.get("title"), context);
            builder.setTitle(title);
        }

        if (embedMap.containsKey("description")) {
            String desc = formatMessage(event, (String) embedMap.get("description"), context);
            builder.setDescription(desc);
        }

        if (embedMap.containsKey("color")) {
            try {
                int color = parseColor(embedMap.get("color"));
                builder.setColor(color);
            } catch (NumberFormatException e) {
                logger.warn("Invalid color format: {}", embedMap.get("color"));
            }
        }

        if (embedMap.containsKey("fields")) {
            List<Map<String, Object>> fields = (List<Map<String, Object>>) embedMap.get("fields");
            for (Map<String, Object> field : fields) {
                String name = formatMessage(event, (String) field.get("name"), context);
                String value = formatMessage(event, (String) field.get("value"), context);
                boolean inline = (Boolean) field.getOrDefault("inline", false);
                builder.addField(name, value, inline);
            }
        }

        if (embedMap.containsKey("author")) {
            Map<String, Object> author = (Map<String, Object>) embedMap.get("author");
            String name = formatMessage(event, (String) author.get("name"), context);
            String url = null;
            if (author.containsKey("url")) {
                String rawUrl = formatMessage(event, (String) author.get("url"), context);
                if (isValidUrl(rawUrl)) url = rawUrl;
            }
            String icon = null;
            if (author.containsKey("icon_url")) {
                String rawIcon = formatMessage(event, (String) author.get("icon_url"), context);
                if (isValidUrl(rawIcon)) icon = rawIcon;
            }
            builder.setAuthor(name, url, icon);
        }

        if (embedMap.containsKey("footer")) {
            Map<String, Object> footer = (Map<String, Object>) embedMap.get("footer");
            String text = formatMessage(event, (String) footer.get("text"), context);
            String icon = null;
            if (footer.containsKey("icon_url")) {
                String rawIcon = formatMessage(event, (String) footer.get("icon_url"), context);
                if (isValidUrl(rawIcon)) icon = rawIcon;
            }
            builder.setFooter(text, icon);
        }

        if (embedMap.containsKey("thumbnail")) {
            String thumb = formatMessage(event, (String) embedMap.get("thumbnail"), context);
            if (isValidUrl(thumb)) builder.setThumbnail(thumb);
        }

        if (embedMap.containsKey("image")) {
            String image = formatMessage(event, (String) embedMap.get("image"), context);
            if (isValidUrl(image)) builder.setImage(image);
        }

        return builder.build();
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) return false;
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private int parseColor(Object color) throws NumberFormatException {
        if (color instanceof Integer) return (Integer) color;
        String str = color.toString().trim();
        if (str.startsWith("#")) str = str.substring(1);
        return Integer.parseInt(str, 16);
    }

    private String formatMessage(SlashCommandInteractionEvent event, String template, Context context) {
        String result = PlaceholdersUser.replace(template != null ? template : "", event, context);
        for (OptionMapping option : event.getOptions()) {
            String placeholder = "{" + option.getName() + "}";
            result = result.replace(placeholder, option.getAsString());
        }
        if (Settings.isDebugSendMessageAction()) {
            logger.info("Formatted message: {}", result);
        }
        return result;
    }
}