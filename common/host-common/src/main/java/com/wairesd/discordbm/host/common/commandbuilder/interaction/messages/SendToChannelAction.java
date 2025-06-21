package com.wairesd.discordbm.host.common.commandbuilder.interaction.messages;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.utils.ContextUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.EmbedFactoryUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SendToChannelAction implements CommandAction {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    
    private final String messageTemplate;
    private final String channelId;
    private final Map<String, Object> embedProperties;
    private final String label;

    public SendToChannelAction(Map<String, Object> properties) {
        this.messageTemplate = (String) properties.get("message");
        this.channelId = (String) properties.get("channel_id");
        this.embedProperties = (Map<String, Object>) properties.get("embed");
        this.label = (String) properties.get("label");
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        ContextUtils.validate(context);
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();

        String targetChannelId = resolveChannelId(event, context);
        if (targetChannelId == null) {
            logger.error("Failed to resolve target channel ID");
            return CompletableFuture.completedFuture(null);
        }
        
        if (Settings.isDebugSendMessageToChannel()) {
            logger.info("SendToChannelAction: Target channel ID = {}", targetChannelId);
        }

        TextChannel channel = event.getJDA().getTextChannelById(targetChannelId);
        if (channel == null) {
            logger.error("Channel with ID {} not found", targetChannelId);
            return CompletableFuture.completedFuture(null);
        }
        
        if (Settings.isDebugSendMessageToChannel()) {
            logger.info("SendToChannelAction: Found channel {}", channel.getName());
        }

        return MessageFormatterUtils.format(messageTemplate, event, context, Settings.isDebugSendMessageAction())
                .thenAccept(formattedMessage -> {
                    String messageText = context.replacePlaceholders(formattedMessage);
                    
                    if (Settings.isDebugSendMessageToChannel()) {
                        logger.info("SendToChannelAction: Sending message to channel {}: '{}'", channel.getName(), messageText);
                    }
                    
                    try {
                        var messageAction = channel.sendMessage(messageText);

                        if (embedProperties != null) {
                            EmbedFactoryUtils.create(embedProperties, event, context)
                                    .thenAccept(embed -> {
                                        if (embed != null) {
                                            messageAction.setEmbeds(embed);
                                        }

                                        messageAction.queue(
                                            message -> {
                                                if (Settings.isDebugSendMessageToChannel()) {
                                                    logger.info("SendToChannelAction: Message successfully sent to channel {}", channel.getName());
                                                }

                                                if (label != null) {
                                                    String fullLabel = (context.getEvent().getGuild() != null ? context.getEvent().getGuild().getId() : "DM") + "_" + label;
                                                    Commands.getPlatform().setGlobalMessageLabel(fullLabel, channel.getId(), message.getId());
                                                }
                                            },
                                            error -> {
                                                if (Settings.isDebugSendMessageToChannel() || Settings.isDebugErrors()) {
                                                    logger.error("SendToChannelAction: Failed to send message to channel {}: {}", 
                                                        channel.getName(), error.getMessage(), error);
                                                }
                                            }
                                        );
                                    });
                        } else {
                            messageAction.queue(
                                message -> {
                                    if (Settings.isDebugSendMessageToChannel()) {
                                        logger.info("SendToChannelAction: Message successfully sent to channel {}", channel.getName());
                                    }

                                    if (label != null) {
                                        String fullLabel = (context.getEvent().getGuild() != null ? context.getEvent().getGuild().getId() : "DM") + "_" + label;
                                        Commands.getPlatform().setGlobalMessageLabel(fullLabel, channel.getId(), message.getId());
                                    }
                                },
                                error -> {
                                    if (Settings.isDebugSendMessageToChannel() || Settings.isDebugErrors()) {
                                        logger.error("SendToChannelAction: Failed to send message to channel {}: {}", 
                                            channel.getName(), error.getMessage(), error);
                                    }
                                }
                            );
                        }
                    } catch (Exception e) {
                        if (Settings.isDebugSendMessageToChannel() || Settings.isDebugErrors()) {
                            logger.error("SendToChannelAction: Exception while sending message: {}", e.getMessage(), e);
                        }
                    }
                });
    }
    
    private String resolveChannelId(SlashCommandInteractionEvent event, Context context) {
        if (channelId != null) {
            return channelId;
        }

        OptionMapping channelOption = event.getOption("channel");
        if (channelOption != null) {
            try {
                return channelOption.getAsChannel().getId();
            } catch (Exception e) {
                if (Settings.isDebugSendMessageToChannel() || Settings.isDebugErrors()) {
                    logger.error("Failed to get channel from option: {}", e.getMessage(), e);
                }
            }
        }
        
        return null;
    }
} 