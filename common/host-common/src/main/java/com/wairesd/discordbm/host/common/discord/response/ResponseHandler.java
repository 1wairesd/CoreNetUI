package com.wairesd.discordbm.host.common.discord.response;

import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.buttons.ButtonStyle;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.DiscordBMVPlatform;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.discord.DiscordBotListener;
import com.wairesd.discordbm.host.common.discord.request.RequestSender;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class ResponseHandler {
    private static DiscordBotListener listener;
    private static DiscordBMVPlatform discordHost;
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));

    public static void init(DiscordBotListener discordBotListener, DiscordBMVPlatform host) {
        listener = discordBotListener;
        discordHost = host;
    }

    public static void handleResponse(ResponseMessage respMsg) {
        if (Settings.isDebugRequestProcessing()) {
            logger.info("Response received for request " + respMsg.requestId() + ": " + respMsg.toString());
        }
        try {
            UUID requestId = UUID.fromString(respMsg.requestId());

            InteractionHook buttonHook = (InteractionHook)discordHost.getPendingButtonRequests().remove(requestId);
            if (buttonHook != null) {
                var embedBuilder = new EmbedBuilder();
                if (respMsg.embed() != null) {
                    embedBuilder.setTitle(respMsg.embed().title())
                            .setDescription(respMsg.embed().description())
                            .setColor(new Color(respMsg.embed().color()));
                }
                var embed = embedBuilder.build();

                List<Button> jdaButtons = respMsg.buttons().stream()
                        .map(btn -> Button.of(getJdaButtonStyle(btn.style()), btn.customId(), btn.label()))
                        .collect(Collectors.toList());

                buttonHook.editOriginalEmbeds(embed)
                        .setActionRow(jdaButtons)
                        .queue();
                return;
            }

            InteractionHook storedHook = listener.getRequestSender().removeInteractionHook(requestId);
            if (storedHook != null) {
                if (Settings.isDebugRequestProcessing()) {
                    logger.info("Found stored hook for requestId: {}", requestId);
                }
                sendResponseWithHook(storedHook, respMsg);
                return;
            }

            var event = listener.getRequestSender().getPendingRequests().remove(requestId);
            if (event == null) {
                logger.warn("No event found for requestId: {}, retrying in 100ms", requestId);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        InteractionHook retryHook = listener.getRequestSender().removeInteractionHook(requestId);
                        if (retryHook != null) {
                            if (Settings.isDebugRequestProcessing()) {
                                logger.info("Found stored hook for requestId: {} on retry", requestId);
                            }
                            sendResponseWithHook(retryHook, respMsg);
                            return;
                        }
                        
                        var retryEvent = listener.getRequestSender().getPendingRequests().remove(requestId);
                        if (retryEvent != null) {
                            if (Settings.isDebugRequestProcessing()) {
                                logger.info("Found event for requestId: {} on retry", requestId);
                            }
                            sendResponse(retryEvent, respMsg);
                        } else {
                            logger.error("Still no event or hook found for requestId: {}", requestId);
                        }
                    }
                }, 100);
                return;
            }
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Found and removed event for requestId: {}", requestId);
            }
            sendResponse(event, respMsg);
        } catch (IllegalArgumentException e) {
            logInvalidUUID(respMsg.requestId(), e);
        }
    }

    private static void sendResponse(SlashCommandInteractionEvent event, ResponseMessage respMsg) {
        if (respMsg.embed() != null) {
            sendCustomEmbed(event, respMsg.embed(), respMsg.buttons(), UUID.fromString(respMsg.requestId()));
        } else if (respMsg.response() != null) {
            event.getHook().sendMessage(respMsg.response()).queue(
                    success -> {
                        if (Settings.isDebugRequestProcessing()) {
                            logger.info("Message sent successfully");
                        }
                    },
                    failure -> logger.error("Failed to send message: {}", failure.getMessage())
            );
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Response sent for requestId: {}", respMsg.requestId());
            }
        } else {
            event.getHook().sendMessage("No response provided.").queue();
        }
    }

    private static void sendCustomEmbed(SlashCommandInteractionEvent event, EmbedDefinition embedDef, List<ButtonDefinition> buttons, UUID requestId) {
        var embedBuilder = new EmbedBuilder();
        if (embedDef.title() != null) {
            embedBuilder.setTitle(embedDef.title());
        }
        if (embedDef.description() != null) {
            Context context = new Context(event);
            
            String serverName = listener.getRequestSender().getServerNameForRequest(requestId);
            if (serverName != null) {
                Map<String, String> variables = new HashMap<>();
                variables.put(RequestSender.SERVER_NAME_VAR, serverName);
                context.setVariables(variables);
            }
            
            String description = embedDef.description();
            try {
                description = MessageFormatterUtils.format(description, event, context, false).get();
            } catch (Exception e) {
                if (Settings.isDebugErrors()) {
                    logger.error("Error formatting embed description: {}", e.getMessage());
                }
            }
            
            embedBuilder.setDescription(description);
        }
        if (embedDef.color() != null) {
            embedBuilder.setColor(new Color(embedDef.color()));
        }
        if (embedDef.fields() != null) {
            for (var field : embedDef.fields()) {
                Context context = new Context(event);
                
                String serverName = listener.getRequestSender().getServerNameForRequest(requestId);
                if (serverName != null) {
                    Map<String, String> variables = new HashMap<>();
                    variables.put(RequestSender.SERVER_NAME_VAR, serverName);
                    context.setVariables(variables);
                }
                
                String fieldName = field.name();
                String fieldValue = field.value();
                
                try {
                    fieldName = MessageFormatterUtils.format(fieldName, event, context, false).get();
                    fieldValue = MessageFormatterUtils.format(fieldValue, event, context, false).get();
                } catch (Exception e) {
                    if (Settings.isDebugErrors()) {
                        logger.error("Error formatting embed field: {}", e.getMessage());
                    }
                }
                
                embedBuilder.addField(fieldName, fieldValue, field.inline());
            }
        }
        var embed = embedBuilder.build();

        if (buttons != null && !buttons.isEmpty()) {
            List<Button> jdaButtons = buttons.stream()
                    .map(btn -> {
                        if (btn.style() == ButtonStyle.LINK) {
                            return Button.link(btn.url(), btn.label());
                        } else {
                            return Button.of(getJdaButtonStyle(btn.style()), btn.customId(), btn.label())
                                    .withDisabled(btn.disabled());
                        }
                    })
                    .collect(Collectors.toList());

            event.getHook().editOriginalEmbeds(embed)
                    .setActionRow(jdaButtons.toArray(new Button[0]))
                    .queue();
        } else {
            if (Settings.isDebugRequestProcessing()) {
                logger.info("About to send embed for requestId: {}", requestId);
            }
            event.getHook().editOriginalEmbeds(embed).queue(
                    success -> {
                        if (Settings.isDebugRequestProcessing()) {
                            logger.info("Successfully sent embed for requestId: {}", requestId);
                        }
                    },
                    failure -> logger.error("Failed to send embed for requestId: {} - {}", requestId, failure.getMessage())
            );
        }
    }

    private static net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle getJdaButtonStyle(ButtonStyle style) {
        return switch (style) {
            case PRIMARY -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.PRIMARY;
            case SECONDARY -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.SECONDARY;
            case SUCCESS -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.SUCCESS;
            case DANGER -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.DANGER;
            case LINK -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.LINK;
        };
    }

    private static void sendResponseWithHook(InteractionHook hook, ResponseMessage respMsg) {
        if (respMsg.embed() != null) {
            var embedBuilder = new EmbedBuilder();
            if (respMsg.embed().title() != null) {
                embedBuilder.setTitle(respMsg.embed().title());
            }
            if (respMsg.embed().description() != null) {
                embedBuilder.setDescription(respMsg.embed().description());
            }
            if (respMsg.embed().color() != null) {
                embedBuilder.setColor(new Color(respMsg.embed().color()));
            }
            if (respMsg.embed().fields() != null) {
                for (var field : respMsg.embed().fields()) {
                    embedBuilder.addField(field.name(), field.value(), field.inline());
                }
            }
            var embed = embedBuilder.build();

            if (respMsg.buttons() != null && !respMsg.buttons().isEmpty()) {
                List<Button> jdaButtons = respMsg.buttons().stream()
                        .map(btn -> {
                            if (btn.style() == ButtonStyle.LINK) {
                                return Button.link(btn.url(), btn.label());
                            } else {
                                return Button.of(getJdaButtonStyle(btn.style()), btn.customId(), btn.label())
                                        .withDisabled(btn.disabled());
                            }
                        })
                        .collect(Collectors.toList());

                hook.editOriginalEmbeds(embed)
                        .setActionRow(jdaButtons.toArray(new Button[0]))
                        .queue();
            } else {
                if (Settings.isDebugRequestProcessing()) {
                    logger.info("About to send embed for requestId: {}", respMsg.requestId());
                }
                hook.editOriginalEmbeds(embed).queue(
                        success -> {
                            if (Settings.isDebugRequestProcessing()) {
                                logger.info("Successfully sent embed for requestId: {}", respMsg.requestId());
                            }
                        },
                        failure -> logger.error("Failed to send embed for requestId: {} - {}", respMsg.requestId(), failure.getMessage())
                );
            }
        } else if (respMsg.response() != null) {
            hook.editOriginal(respMsg.response()).queue(
                    success -> {
                        if (Settings.isDebugRequestProcessing()) {
                            logger.info("Message sent successfully");
                        }
                    },
                    failure -> logger.error("Failed to send message: {}", failure.getMessage())
            );
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Response sent for requestId: {}", respMsg.requestId());
            }
        } else {
            hook.editOriginal("No response provided.").queue();
        }
    }

    private static void logInvalidUUID(String requestIdStr, IllegalArgumentException e) {
        if (Settings.isDebugErrors()) {
            logger.error("Invalid UUID in response: {}", requestIdStr, e);
        }
    }
}