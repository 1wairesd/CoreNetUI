package com.wairesd.discordbm.host.common.api;

import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.form.Form;
import com.wairesd.discordbm.api.command.CommandCondition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.models.response.ResponseFlags;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.form.FormDefinition;
import com.wairesd.discordbm.host.common.discord.response.ResponseHandler;
import com.wairesd.discordbm.host.common.config.configurators.Webhooks;
import com.wairesd.discordbm.host.common.utils.WebhookSender;
import com.wairesd.discordbm.common.component.ButtonAdapter;
import com.wairesd.discordbm.common.form.FormAdapter;
import com.wairesd.discordbm.common.embed.EmbedAdapter;
import net.dv8tion.jda.api.JDA;
import java.util.List;
import java.util.UUID;
import com.google.gson.JsonObject;

public class HostMessageSender implements MessageSender {
    private final JDA jda;

    public HostMessageSender(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void sendResponse(String requestId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, Embed embed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .embed(convertToEmbedDefinition(embed))
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendForm(String requestId, Form form) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .form(convertToFormDefinition(form))
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendForm(String requestId, String message, Form form) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .form(convertToFormDefinition(form))
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendDirectMessage(String userId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(message)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendDirectMessage(String userId, Embed embed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .embed(convertToEmbedDefinition(embed))
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(UUID.randomUUID().toString())
                .response(message)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessage(String channelId, Embed embed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .embed(convertToEmbedDefinition(embed))
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, String message, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .requestId(label)
                .response(message)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .embed(convertToEmbedDefinition(embed))
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void editMessage(String label, String newMessage) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .response(newMessage)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void editMessage(String label, Embed newEmbed) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .embed(convertToEmbedDefinition(newEmbed))
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void editMessage(String label, Embed newEmbed, List<Button> newButtons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .embed(convertToEmbedDefinition(newEmbed))
                .buttons(convertToButtonDefinitions(newButtons))
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.editComponent(respMsg);
    }

    @Override
    public void deleteMessage(String label, boolean deleteAll) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("delete_message")
                .requestId(label)
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().responseType("REPLY_TO_MESSAGE").build())
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
    public void sendResponseWithConditions(String requestId, String message, List<CommandCondition> conditions) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .conditions(serialized)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendResponseWithConditions(String requestId, String message, List<CommandCondition> conditions, String label) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .response(message)
                .conditions(serialized)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessageWithConditions(String channelId, String message, List<CommandCondition> conditions) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(message)
                .conditions(serialized)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendChannelMessageWithConditions(String channelId, String message, List<CommandCondition> conditions, String label) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(message)
                .conditions(serialized)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendButtonWithForm(String requestId, String message, Button button, Form form) {
        ButtonDefinition buttonDef = button != null ? new ButtonAdapter(button).getInternalButton() : null;
        FormDefinition formDef = convertToFormDefinition(form);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .buttons(buttonDef != null ? java.util.Collections.singletonList(buttonDef) : null)
                .form(formDef)
                .flags(new ResponseFlags.Builder().build())
                .build();
        ResponseHandler.handleResponse(respMsg);
    }

    @Override
    public void sendRandomReply(String requestId, List<String> messages) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .responses(messages)
                .flags(new ResponseFlags.Builder().responseType("RANDOM_REPLY").build())
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
    private FormDefinition convertToFormDefinition(Form form) {
        if (form == null) return null;
        return new FormAdapter(form).getInternalForm();
    }
} 