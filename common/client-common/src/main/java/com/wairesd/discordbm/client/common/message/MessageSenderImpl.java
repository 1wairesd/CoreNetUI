package com.wairesd.discordbm.client.common.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.form.Form;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.common.component.ButtonAdapter;
import com.wairesd.discordbm.common.embed.EmbedAdapter;
import com.wairesd.discordbm.common.form.FormAdapter;
import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.form.FormDefinition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.models.response.ResponseFlags;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.api.message.ResponseType;

import java.util.List;
import java.util.stream.Collectors;

public class MessageSenderImpl implements MessageSender {
    
    private final Platform platform;
    private final Logger logger;
    private final Gson gson = new Gson();
    private ResponseType currentResponseType;

    public MessageSenderImpl(Platform platform, Logger logger) {
        this.platform = platform;
        this.logger = logger;
    }
    
    @Override
    public void sendResponse(String requestId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendResponse(String requestId, Embed embed) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendResponse(String requestId, Embed embed, List<Button> buttons) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(null)
                .embed(embedDef)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendForm(String requestId, Form form) {
        FormDefinition formDef = convertToFormDefinition(form);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(null)
                .embed(null)
                .buttons(null)
                .form(formDef)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendForm(String requestId, String message, Form form) {
        FormDefinition formDef = convertToFormDefinition(form);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(formDef)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendDirectMessage(String userId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendDirectMessage(String userId, Embed embed) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendDirectMessage(String userId, Embed embed, List<Button> buttons) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(null)
                .embed(embedDef)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendDirectMessage(String userId, String message, List<Button> buttons) {
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(message)
                .embed(null)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendChannelMessage(String channelId, String message) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendChannelMessage(String channelId, Embed embed) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendChannelMessage(String channelId, Embed embed, List<Button> buttons) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(null)
                .embed(embedDef)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

        @Override
    public void sendChannelMessage(String channelId, String message, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendChannelMessage(String channelId, Embed embed, String label) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendChannelMessage(String channelId, String message, List<Button> buttons, String label) {
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(message)
                .embed(null)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
    
    @Override
    public void sendChannelMessage(String channelId, Embed embed, List<Button> buttons, String label) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(null)
                .embed(embedDef)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendChannelMessage(String channelId, String message, List<Button> buttons) {
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(message)
                .embed(null)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, String message, List<Button> buttons) {
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .embed(null)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, String message, String label) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, String label) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, List<Button> buttons, String label) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .response(null)
                .embed(embedDef)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, String message, List<Button> buttons, String label) {
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .response(message)
                .embed(null)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void editMessage(String label, String newMessage) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .response(newMessage)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void editMessage(String label, Embed newEmbed) {
        EmbedDefinition embedDef = convertToEmbedDefinition(newEmbed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void editMessage(String label, Embed newEmbed, List<Button> newButtons) {
        EmbedDefinition embedDef = convertToEmbedDefinition(newEmbed);
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(newButtons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("edit_message")
                .requestId(label)
                .response(null)
                .embed(embedDef)
                .buttons(buttonDefs)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
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
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendDirectMessage(String userId, String message, String requestId, String channelId) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("direct_message")
                .userId(userId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .requestId(requestId)
                .channelId(channelId)
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void deleteMessage(String label, boolean deleteAll) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "delete_message");
        obj.addProperty("label", label);
        if (!deleteAll) obj.addProperty("delete_all", false);
        platform.getNettyService().sendNettyMessage(obj.toString());
    }

    @Override
    public void deleteMessage(String label) {
        deleteMessage(label, true);
    }

    private EmbedDefinition convertToEmbedDefinition(Embed embed) {
        if (embed == null) {
            return null;
        }
        return new EmbedAdapter(embed).getInternalEmbed();
    }

    private List<ButtonDefinition> convertToButtonDefinitions(List<Button> buttons) {
        if (buttons == null) {
            return null;
        }
        return buttons.stream()
                .map(button -> new ButtonAdapter(button).getInternalButton())
                .collect(Collectors.toList());
    }
    
    private FormDefinition convertToFormDefinition(Form form) {
        if (form == null) {
            return null;
        }
        return new FormAdapter(form).getInternalForm();
    }
    
    private ResponseFlags createResponseFlags() {
        ResponseFlags.Builder flagsBuilder = new ResponseFlags.Builder();
        
        if (currentResponseType != null) {
            flagsBuilder.responseType(currentResponseType.name());
            
            // Устанавливаем специальные флаги в зависимости от типа
            switch (currentResponseType) {
                case MODAL:
                    flagsBuilder.requiresModal(true).preventMessageSend(true).isFormResponse(true);
                    break;
                case REPLY_MODAL:
                    flagsBuilder.isFormResponse(true).requiresModal(false);
                    break;
                case EDIT_MESSAGE:
                    flagsBuilder.preventMessageSend(false).isFormResponse(false).requiresModal(false);
                    break;
                case DIRECT:
                case CHANNEL:
                    flagsBuilder.preventMessageSend(false).isFormResponse(false).requiresModal(false);
                    break;
                case REPLY_TO_MESSAGE:
                    flagsBuilder.preventMessageSend(false).isFormResponse(false).requiresModal(false);
                    break;
                case REPLY:
                default:
                    flagsBuilder.preventMessageSend(false).isFormResponse(false).requiresModal(false);
                    break;
            }
        }
        
        return flagsBuilder.build();
    }
    
    public void setResponseType(ResponseType responseType) {
        this.currentResponseType = responseType;
    }
    
    public ResponseType getCurrentResponseType() {
        return currentResponseType;
    }
    
    public void clearResponseType() {
        this.currentResponseType = null;
    }




    @Override
    public void sendButtonWithForm(String requestId, String message, Button button, Form form) {
        ButtonDefinition buttonDef = new ButtonAdapter(button).getInternalButton();
        if (form == null) throw new IllegalArgumentException("Form must not be null for sendButtonWithForm");
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .embed(null)
                .buttons(java.util.Collections.singletonList(buttonDef))
                .form(convertToFormDefinition(form))
                .flags(createResponseFlags())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendRandomReply(String requestId, List<String> messages) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .responses(messages)
                .flags(new ResponseFlags.Builder().responseType("RANDOM_REPLY").build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
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
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendWebhook(String webhookName, String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "send_webhook");
        obj.addProperty("webhookName", webhookName);
        obj.addProperty("message", message);
        platform.getNettyService().sendNettyMessage(obj.toString());
    }
}