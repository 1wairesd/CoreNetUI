package com.wairesd.discordbm.client.common.message;

import com.google.gson.Gson;
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
    public void sendResponseWithButtons(String requestId, Embed embed, List<Button> buttons) {
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
    public void sendFormWithMessage(String requestId, String message, Form form) {
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
        // Implementation for sending direct messages
        // This would require additional functionality in the platform
    }
    
    @Override
    public void sendDirectMessage(String userId, Embed embed) {
        // Implementation for sending direct messages with embeds
        // This would require additional functionality in the platform
    }
    
    @Override
    public void sendChannelMessage(String channelId, String message) {
        // Implementation for sending channel messages
        // This would require additional functionality in the platform
    }
    
    @Override
    public void sendChannelMessage(String channelId, Embed embed) {
        // Implementation for sending channel messages with embeds
        // This would require additional functionality in the platform
    }
    
    @Override
    public void sendChannelMessageWithButtons(String channelId, Embed embed, List<Button> buttons) {
        // Implementation for sending channel messages with embeds and buttons
        // This would require additional functionality in the platform
    }

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
    public void sendResponseWithButtons(String requestId, Embed embed, List<Button> buttons, boolean ephemeral) {
        EmbedDefinition embedDef = convertToEmbedDefinition(embed);
        List<ButtonDefinition> buttonDefs = convertToButtonDefinitions(buttons);
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(null)
                .embed(embedDef)
                .buttons(buttonDefs)
                .form(null)
                .flags(new ResponseFlags.Builder().ephemeral(ephemeral).build())
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
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
} 