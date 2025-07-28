package com.wairesd.discordbm.host.common.api;

import com.google.gson.JsonObject;
import com.wairesd.discordbm.api.modal.Modal;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.api.message.ResponseType;
import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.common.modal.ModalAdapter;
import com.wairesd.discordbm.common.models.modal.ModalDefinition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.models.response.ResponseFlags;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.embed.EmbedAdapter;
import com.wairesd.discordbm.common.component.ButtonAdapter;
import com.wairesd.discordbm.host.common.discord.response.ResponseHandler;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import com.wairesd.discordbm.host.common.utils.WebhookSender;
import net.dv8tion.jda.api.JDA;

import java.util.List;

public class HostMessageSender implements MessageSender {
    private final JDA jda;
    private final HostDiscordBMAPIImpl dbmApi;

    public HostMessageSender(JDA jda, HostDiscordBMAPIImpl dbmApi) {
        this.jda = jda;
        this.dbmApi = dbmApi;
    }

    private ResponseFlags createResponseFlags() {
        ResponseFlags.Builder flagsBuilder = new ResponseFlags.Builder();
        
        ResponseType currentResponseType = dbmApi.getCurrentResponseType();
        if (currentResponseType != null) {
            flagsBuilder.responseType(currentResponseType.name());
        }
        
        boolean currentEphemeral = dbmApi.getCurrentEphemeral();
        flagsBuilder.ephemeral(currentEphemeral);
        
        return flagsBuilder.build();
    }

    @Override
    public void sendResponse(String requestId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, Embed embed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .embed(convertToEmbedDefinition(embed))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, List<Button> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .embed(convertToEmbedDefinition(embed))
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, String message, List<Button> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendModal(String requestId, Modal modal) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .modal(convertToFormDefinition(modal))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendModal(String requestId, String message, Modal modal) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .modal(convertToFormDefinition(modal))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendDirectMessage(String userId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(message)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendDirectMessage(String userId, String message, String requestId, String channelId) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(message)
                .requestId(requestId)
                .channelId(channelId)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendDirectMessage(String userId, String message, List<Button> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(message)
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendDirectMessage(String userId, Embed embed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .embed(convertToEmbedDefinition(embed))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendDirectMessage(String userId, Embed embed, List<Button> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .embed(convertToEmbedDefinition(embed))
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(message)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, String message, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(message)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, Embed embed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .embed(convertToEmbedDefinition(embed))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, Embed embed, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .embed(convertToEmbedDefinition(embed))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, Embed embed, List<Button> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .embed(convertToEmbedDefinition(embed))
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, Embed embed, List<Button> buttons, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .embed(convertToEmbedDefinition(embed))
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, String message, List<Button> buttons, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(message)
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, String message, List<Button> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(message)
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, String message, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .requestId(label)
                .response(message)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .embed(convertToEmbedDefinition(embed))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, List<Button> buttons, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .embed(convertToEmbedDefinition(embed))
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, String message, List<Button> buttons, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .response(message)
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void editMessage(String label, String newMessage) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .response(newMessage)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void editMessage(String label, Embed newEmbed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .embed(convertToEmbedDefinition(newEmbed))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void editMessage(String label, Embed newEmbed, List<Button> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .embed(convertToEmbedDefinition(newEmbed))
                .buttons(convertToButtonDefinitions(buttons))
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void editComponent(String label, String componentId, String newLabel, String newStyle, Boolean disabled) {
        JsonObject responseObj = new JsonObject();
        responseObj.addProperty("componentId", componentId);
        if (newLabel != null) responseObj.addProperty("newLabel", newLabel);
        if (newStyle != null) responseObj.addProperty("newStyle", newStyle);
        if (disabled != null) responseObj.addProperty("disabled", disabled);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_component")
                .requestId(label)
                .response(responseObj.toString())
                .flags(createResponseFlags())
                .build();
        ResponseHandler.editComponent(respMsg);
    }

    @Override
    public void deleteMessage(String label, boolean deleteAll) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("delete_message")
                .requestId(label)
                .flags(createResponseFlags())
                .deleteAll(deleteAll)
                .build();
        ResponseHandler.deleteMessage(respMsg);
    }

    @Override
    public void deleteMessage(String label) {
        deleteMessage(label, true);
    }

    @Override
    public void sendReplyToMessage(String requestId, String message, String replyMessageId, boolean mentionAuthor) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .replyMessageId(replyMessageId)
                .replyMentionAuthor(mentionAuthor)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendWebhook(String webhookName, String message) {
        var webhook = Webhooks.getWebhooks().stream()
            .filter(w -> w.name().equals(webhookName) && w.enabled())
            .findFirst()
            .orElse(null);
        if (webhook == null) {
            throw new IllegalArgumentException("Webhook not found or not enabled: " + webhookName);
        }
        WebhookSender.sendWebhook(webhook.url(), message);
    }

    @Override
    public void sendButtonWithModal(String requestId, String message, Button button, Modal modal) {
        ButtonDefinition buttonDef = button != null ? new ButtonAdapter(button).getInternalButton() : null;
        ModalDefinition formDef = convertToFormDefinition(modal);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .buttons(buttonDef != null ? java.util.Collections.singletonList(buttonDef) : null)
                .modal(formDef)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendRandomReply(String requestId, List<String> messages) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .responses(messages)
                .flags(createResponseFlags())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    private EmbedDefinition convertToEmbedDefinition(Embed embed) {
        if (embed == null) return null;
        return new EmbedAdapter(embed).getInternalEmbed();
    }
    private List<ButtonDefinition> convertToButtonDefinitions(List<Button> buttons) {
        if (buttons == null) return null;
        return buttons.stream().map(b -> new ButtonAdapter(b).getInternalButton()).toList();
    }
    private ModalDefinition convertToFormDefinition(Modal modal) {
        if (modal == null) return null;
        return new ModalAdapter(modal).getInternalModal();
    }
} 