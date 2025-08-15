package com.wairesd.discordbm.host.common.discord.response.Interaction;

import com.wairesd.discordbm.common.models.buttons.ButtonStyle;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.registry.ButtonActionRegistry;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.discord.DiscordBotListener;
import com.wairesd.discordbm.host.common.discord.response.ResponseHandler;
import com.wairesd.discordbm.host.common.utils.Components;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SlashCommandInteraction {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final Random random = new Random();

    private static DiscordBotListener listener;
    private static DiscordBMHPlatformManager platformManager;

    public static void SlashCommandInteraction(SlashCommandInteractionEvent event, ResponseMessage respMsg) {
        boolean ephemeral = isEphemeralResponse(respMsg);
        String responseType = getResponseType(respMsg);
        String label = respMsg.requestId();

        if ("RANDOM_REPLY".equalsIgnoreCase(responseType)) {
            handleRandomReply(event, respMsg, ephemeral, label);
            return;
        }

        if ("EDIT_MESSAGE".equalsIgnoreCase(responseType)) {
            handleEditMessage(event, respMsg);
            return;
        }

        if ("REPLY_TO_MESSAGE".equalsIgnoreCase(responseType)) {
            handleReplyToMessage(event, respMsg, label);
            return;
        }

        handleStandardResponse(event, respMsg, ephemeral, label);
        handleButtonRegistration(respMsg);
    }

    private static boolean isEphemeralResponse(ResponseMessage respMsg) {
        return respMsg.flags() != null && respMsg.flags().isEphemeral();
    }

    private static String getResponseType(ResponseMessage respMsg) {
        return respMsg.flags() != null ? respMsg.flags().getResponseType() : null;
    }

    private static void handleRandomReply(SlashCommandInteractionEvent event, ResponseMessage respMsg,
                                          boolean ephemeral, String label) {
        if (respMsg.responses() == null || respMsg.responses().isEmpty()) {
            return;
        }

        List<String> responses = respMsg.responses();
        String randomMessage = responses.get(random.nextInt(responses.size()));

        Consumer<net.dv8tion.jda.api.entities.Message> successCallback = createSuccessCallback(event, label);
        Consumer<Throwable> failureCallback = failure ->
                logger.error("Failed to send random reply message: {}", failure.getMessage());

        try {
            event.getHook().sendMessage(randomMessage)
                    .setEphemeral(ephemeral)
                    .queue(successCallback, failureCallback);
        } catch (Exception e) {
            logger.error("Exception while sending message for requestId: {} | {}",
                    respMsg.requestId(), e.getMessage(), e);
        }
    }

    private static void handleEditMessage(SlashCommandInteractionEvent event, ResponseMessage respMsg) {
        String message = respMsg.response() != null ? respMsg.response() : "";
        event.getHook().editOriginal(message).queue();
    }

    private static void handleReplyToMessage(SlashCommandInteractionEvent event, ResponseMessage respMsg, String label) {
        if (respMsg.replyMessageId() == null || respMsg.replyMessageId().isEmpty()) {
            return;
        }

        try {
            deleteOriginalMessage(event);
            sendReplyMessage(event, respMsg, label);
        } catch (Exception e) {
            logger.error("Exception while sending REPLY_TO_MESSAGE for requestId: {} | {}",
                    respMsg.requestId(), e.getMessage(), e);
        }
    }

    private static void deleteOriginalMessage(SlashCommandInteractionEvent event) {
        if (event == null) return;

        if (!event.isAcknowledged()) {
            event.deferReply(true).queue(hook -> hook.deleteOriginal().queue());
        } else if (event.getHook() != null) {
            event.getHook().deleteOriginal().queue();
        }
    }

    private static void sendReplyMessage(SlashCommandInteractionEvent event, ResponseMessage respMsg, String label) {
        var msgAction = event.getChannel().sendMessage(respMsg.response())
                .setMessageReference(respMsg.replyMessageId())
                .mentionRepliedUser(Boolean.TRUE.equals(respMsg.replyMentionAuthor()));

        Consumer<net.dv8tion.jda.api.entities.Message> successCallback = createSuccessCallback(event, label);
        Consumer<Throwable> failureCallback = failure ->
                logger.error("Failed to send REPLY_TO_MESSAGE: {}", failure.getMessage());

        msgAction.queue(successCallback, failureCallback);
    }

    private static void handleStandardResponse(SlashCommandInteractionEvent event, ResponseMessage respMsg,
                                               boolean ephemeral, String label) {
        if (respMsg.embed() != null) {
            handleEmbedResponse(event, respMsg, ephemeral);
        } else if (respMsg.response() != null) {
            handleTextResponse(event, respMsg, ephemeral, label);
        } else {
            handleNoResponse(event, ephemeral);
        }
    }

    private static void handleEmbedResponse(SlashCommandInteractionEvent event, ResponseMessage respMsg, boolean ephemeral) {
        ResponseHandler.sendEmbed(event, respMsg.embed(), respMsg.buttons(),
                UUID.fromString(respMsg.requestId()), ephemeral);
    }

    private static void handleTextResponse(SlashCommandInteractionEvent event, ResponseMessage respMsg,
                                           boolean ephemeral, String label) {
        if (respMsg.response().startsWith("ERROR:")) {
            ResponseHandler.handleConditionError(event, respMsg.response(), ephemeral);
            return;
        }

        if (hasButtons(respMsg)) {
            sendMessageWithButtons(event, respMsg, ephemeral, label);
        } else {
            sendSimpleMessage(event, respMsg, ephemeral, label);
        }

        logResponseSent(respMsg);
        removePendingRequest(respMsg);
    }

    private static void handleNoResponse(SlashCommandInteractionEvent event, boolean ephemeral) {
        event.getHook().sendMessage("No response provided.").setEphemeral(ephemeral).queue();
    }

    private static boolean hasButtons(ResponseMessage respMsg) {
        return respMsg.buttons() != null && !respMsg.buttons().isEmpty();
    }

    private static void sendMessageWithButtons(SlashCommandInteractionEvent event, ResponseMessage respMsg,
                                               boolean ephemeral, String label) {
        List<Button> jdaButtons = createJdaButtons(respMsg);

        Consumer<net.dv8tion.jda.api.entities.Message> successCallback = success -> {
            handleMessageSuccess(event, label);
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Message with buttons sent successfully");
            }
        };
        Consumer<Throwable> failureCallback = failure ->
                logger.error("Failed to send message with buttons: {}", failure.getMessage());

        event.getHook().sendMessage(respMsg.response())
                .addActionRow(jdaButtons)
                .setEphemeral(ephemeral)
                .queue(successCallback, failureCallback);
    }

    private static void sendSimpleMessage(SlashCommandInteractionEvent event, ResponseMessage respMsg,
                                          boolean ephemeral, String label) {
        Consumer<net.dv8tion.jda.api.entities.Message> successCallback = success -> {
            handleMessageSuccess(event, label);
            if (Settings.isDebugRequestProcessing()) {
                logger.info("Message sent successfully");
            }
        };
        Consumer<Throwable> failureCallback = failure ->
                logger.error("Failed to send message: {}", failure.getMessage());

        event.getHook().sendMessage(respMsg.response())
                .setEphemeral(ephemeral)
                .queue(successCallback, failureCallback);
    }

    private static List<Button> createJdaButtons(ResponseMessage respMsg) {
        return respMsg.buttons().stream()
                .map(btn -> {
                    if (btn.style() == ButtonStyle.LINK) {
                        return Button.link(btn.url(), btn.label());
                    } else {
                        return Button.of(Components.getJdaButtonStyle(btn.style()), btn.customId(), btn.label())
                                .withDisabled(btn.disabled());
                    }
                })
                .collect(Collectors.toList());
    }

    private static Consumer<net.dv8tion.jda.api.entities.Message> createSuccessCallback(
            SlashCommandInteractionEvent event, String label) {
        return success -> handleMessageSuccess(event, label);
    }

    private static void handleMessageSuccess(SlashCommandInteractionEvent event, String label) {
        if (label != null && !label.isEmpty()) {
            String channelId = event.getChannel().getId();
            String messageId = event.getHook().getInteraction().getId();
            platformManager.setGlobalMessageLabel(label, channelId, messageId);
        }
    }

    private static void logResponseSent(ResponseMessage respMsg) {
        if (Settings.isDebugRequestProcessing()) {
            logger.info("Response sent for requestId: {}", respMsg.requestId());
        }
    }

    private static void removePendingRequest(ResponseMessage respMsg) {
        UUID requestId = UUID.fromString(respMsg.requestId());
        listener.getRequestSender().getPendingRequests().remove(requestId);
    }

    private static void handleButtonRegistration(ResponseMessage respMsg) {
        if (!hasButtons(respMsg) || respMsg.modal() == null) {
            return;
        }

        ButtonActionRegistry registry = new ButtonActionRegistry();
        for (var btn : respMsg.buttons()) {
            if (btn.formName() != null && !btn.formName().isEmpty()) {
                registry.registerFormButton(
                        btn.customId(),
                        btn.formName(),
                        respMsg.response(),
                        null,
                        10 * 60 * 1000L
                );
            }
        }
    }
}