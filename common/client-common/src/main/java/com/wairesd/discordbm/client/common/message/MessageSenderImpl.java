package com.wairesd.discordbm.client.common.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.form.Form;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.client.common.component.ButtonAdapter;
import com.wairesd.discordbm.client.common.embed.EmbedAdapter;
import com.wairesd.discordbm.client.common.form.FormAdapter;
import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.form.FormDefinition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.models.response.ResponseFlags;
import com.wairesd.discordbm.client.common.platform.Platform;

import java.util.List;
import java.util.stream.Collectors;

public class MessageSenderImpl implements MessageSender {
    
    private final Platform platform;
    private final Logger logger;
    private final Gson gson = new Gson();

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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder()
                        .preventMessageSend(true)
                        .isFormResponse(true)
                        .requiresModal(true)
                        .build())
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
                .flags(new ResponseFlags.Builder()
                        .preventMessageSend(true)
                        .isFormResponse(true)
                        .requiresModal(true)
                        .build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
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
                .flags(new ResponseFlags.Builder().build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void deleteMessage(String label) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "delete_message");
        obj.addProperty("label", label);
        platform.getNettyService().sendNettyMessage(obj.toString());
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

    @Override
    public void sendResponseWithConditions(String requestId, String message, List<com.wairesd.discordbm.api.command.CommandCondition> conditions) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(com.wairesd.discordbm.api.command.CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().build())
                .conditions(serialized)
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponseWithConditions(String requestId, String message, List<com.wairesd.discordbm.api.command.CommandCondition> conditions, String label) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(com.wairesd.discordbm.api.command.CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(label)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().build())
                .conditions(serialized)
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendChannelMessageWithConditions(String channelId, String message, List<com.wairesd.discordbm.api.command.CommandCondition> conditions) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(com.wairesd.discordbm.api.command.CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().build())
                .conditions(serialized)
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendChannelMessageWithConditions(String channelId, String message, List<com.wairesd.discordbm.api.command.CommandCondition> conditions, String label) {
        List<java.util.Map<String, Object>> serialized = null;
        if (conditions != null) {
            serialized = conditions.stream().map(com.wairesd.discordbm.api.command.CommandCondition::serialize).toList();
        }
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("channel_message")
                .channelId(channelId)
                .requestId(label)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().build())
                .conditions(serialized)
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
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
                .flags(new ResponseFlags.Builder()
                        .preventMessageSend(true)
                        .isFormResponse(false)
                        .requiresModal(false)
                        .build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, String message, boolean ephemeral) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().ephemeral(ephemeral).build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, boolean ephemeral) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().ephemeral(ephemeral).build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, String message, ResponseType responseType) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(message)
                .embed(null)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().responseType(responseType.name()).build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    @Override
    public void sendResponse(String requestId, Embed embed, ResponseType responseType) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(null)
                .embed(embedDef)
                .buttons(null)
                .form(null)
                .flags(new ResponseFlags.Builder().responseType(responseType.name()).build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }
}