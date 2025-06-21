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
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

public class SendMessageAction implements CommandAction {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private static final String DEFAULT_MESSAGE = "";

    private final String messageTemplate;
    private final ResponseType responseType;
    private final String targetId;
    private final Map<String, Object> embedProperties;
    private final String label;

    public SendMessageAction(Map<String, Object> properties) {
        SendMessageValidator.validate(properties);
        this.messageTemplate = (String) properties.getOrDefault("message", DEFAULT_MESSAGE);
        this.embedProperties = (Map<String, Object>) properties.get("embed");
        this.responseType = ResponseType.valueOf(((String) properties.getOrDefault("response_type", "REPLY")).toUpperCase());
        this.targetId = (String) properties.get("target_id");
        this.label = (String) properties.get("label");
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

        return MessageFormatterUtils.format(messageTemplate, event, context, Settings.isDebugSendMessageAction())
                .thenAccept(formattedMessage -> {
                    context.setMessageText(formattedMessage);
                    context.setResponseType(responseType);

                    if (embedProperties != null) {
                        EmbedFactoryUtils.create(embedProperties, event, context)
                                .thenAccept(context::setEmbed);
                    }

                    if (this.label != null) {
                        context.setExpectedMessageLabel(this.label);
                    }

                    ResponseStrategy strategy = ResponseStrategyFactory.getStrategy(responseType);
                    strategy.apply(context, formattedTargetId);
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