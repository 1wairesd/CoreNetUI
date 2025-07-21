package com.wairesd.discordbm.host.common.commandbuilder.interaction.messages;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.ResponseType;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.strategy.ResponseStrategy;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.strategy.ResponseStrategyFactory;
import com.wairesd.discordbm.host.common.commandbuilder.utils.ContextUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.EmbedFactoryUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.TargetIDResolverUtils;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.validator.SendMessageValidator;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import java.util.Random;
import java.util.List;

public class SendMessageAction implements CommandAction {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final String DEFAULT_MESSAGE = "";

    private final String messageTemplate;
    private final ResponseType responseType;
    private final String targetId;
    private final Map<String, Object> embedProperties;
    private final String label;
    private final String replyMessageId;
    private final boolean replyMentionAuthor;
    private final List<String> messageList;
    private static final Random RANDOM = new Random();

    public SendMessageAction(Map<String, Object> properties) {
        SendMessageValidator.validate(properties);
        Object msgObj = properties.get("message");
        if (msgObj instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof String) {
            this.messageList = (List<String>) list;
            this.messageTemplate = this.messageList.get(0);
        } else {
            this.messageList = null;
            this.messageTemplate = (String) properties.getOrDefault("message", DEFAULT_MESSAGE);
        }
        this.embedProperties = (Map<String, Object>) properties.get("embed");
        String respTypeStr = (String) properties.getOrDefault("response_type", "REPLY");
        ResponseType respType;
        try {
            respType = ResponseType.valueOf(respTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            respType = ResponseType.REPLY;
        }
        this.responseType = respType;
        this.targetId = (String) properties.get("target_id");
        this.label = (String) properties.get("label");
        this.replyMessageId = (String) properties.get("reply_message_id");
        this.replyMentionAuthor = properties.get("reply_mention_author") instanceof Boolean ? (Boolean) properties.get("reply_mention_author") : false;
    }

    public String getReplyMessageId() {
        return replyMessageId;
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        ContextUtils.validate(context);
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();

        OptionMapping targetOption = event.getOption("target");
        if (targetOption != null) {
            try {
                User targetUser = targetOption.getAsUser();
                if (targetUser != null) {
                    context.setTargetUser(targetUser);
                }
            } catch (IllegalStateException e) {
                logger.warn("Failed to resolve user from target option: {}", e.getMessage());
            }
        }

        final String formattedTargetId = resolveTargetId(event, context);

        return MessageFormatterUtils.format(
            (responseType == ResponseType.RANDOM_REPLY && messageList != null && !messageList.isEmpty())
                ? messageList.get(RANDOM.nextInt(messageList.size()))
                : messageTemplate,
            event, context, Settings.isDebugSendMessageAction())
                .thenAccept(formattedMessage -> {
                    if (responseType == ResponseType.RANDOM_REPLY && messageList != null && !messageList.isEmpty()) {
                        context.setMessageList(messageList);
                        context.setMessageText("");
                    } else if (responseType == ResponseType.REPLY_TO_MESSAGE) {
                        context.setMessageText(formattedMessage);
                        if (context.getVariables() != null) {
                            context.getVariables().put("reply_message_id", replyMessageId);
                            context.getVariables().put("reply_mention_author", Boolean.toString(replyMentionAuthor));
                        }
                    } else {
                        context.setMessageText(formattedMessage);
                        context.setMessageList(null);
                    }
                    context.setResponseType(responseType);

                    if (embedProperties != null) {
                        EmbedFactoryUtils.create(embedProperties, event, context)
                                .thenAccept(context::setEmbed);
                    }

                    if (this.label != null) {
                        context.setExpectedMessageLabel(this.label);
                    }

                    if (replyMessageId != null && !replyMessageId.isEmpty()) {
                        try {
                            String replyId = context.replacePlaceholders(replyMessageId);
                            event.deferReply(true).queue(hook -> hook.deleteOriginal().queue());
                            var msgAction = event.getChannel().sendMessage(formattedMessage)
                                .setMessageReference(replyId)
                                .mentionRepliedUser(replyMentionAuthor);
                            if (context.getEmbed() != null) msgAction.setEmbeds(context.getEmbed());
                            if (!context.getActionRows().isEmpty()) msgAction.setComponents(context.getActionRows());
                            msgAction.queue(m -> {
                                if (this.label != null) {
                                    String fullLabel = (event.getGuild() != null ? event.getGuild().getId() : "DM") + "_" + this.label;
                                    Commands.getPlatform().setGlobalMessageLabel(fullLabel, m.getChannel().getId(), m.getId());
                                }
                            });
                        } catch (Exception e) {
                        }
                    } else {
                        ResponseStrategy strategy = ResponseStrategyFactory.getStrategy(responseType);
                        strategy.apply(context, formattedTargetId);
                    }
                });
    }
    
    private String resolveTargetId(SlashCommandInteractionEvent event, Context context) {
        if (responseType == ResponseType.SPECIFIC_CHANNEL && "{channel}".equals(this.targetId)) {
            OptionMapping channelOption = event.getOption("channel");
            if (channelOption != null) {
                try {
                    Channel channel = channelOption.getAsChannel();
                    if (channel != null) {
                        String channelId = channel.getId();
                        if (Settings.isDebugSendMessageToChannel()) {
                            logger.info("Sending message to channel: {} ({})", channel.getName(), channelId);
                        }
                        return channelId;
                    }
                } catch (Exception e) {
                    if (Settings.isDebugSendMessageToChannel() || Settings.isDebugErrors()) {
                        logger.error("Error resolving channel from option: {}", e.getMessage(), e);
                    }
                }
            }
        }
        return TargetIDResolverUtils.resolve(event, this.targetId, context);
    }
}