package com.wairesd.discordbm.host.common.discord.response.handler.editor.option;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.component.ButtonEditor;
import com.wairesd.discordbm.host.common.commandbuilder.utils.message.MessageComponentFetcher;
import com.wairesd.discordbm.host.common.commandbuilder.utils.message.MessageUpdater;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;

public class Component {
    private static DiscordBMHPlatformManager platformManager;

    public static void editComponent(ResponseMessage respMsg) {
        String label = respMsg.requestId();
        if (respMsg.type() != null && respMsg.type().equals("edit_component")) {
            label = respMsg.response();
        }
        if (label == null) {
            return;
        }
        String[] ref = platformManager.getMessageReference(label);
        if (ref == null || ref.length != 2) {
            return;
        }
        String channelId = ref[0];
        String messageId = ref[1];
        var jda = platformManager.getDiscordBotManager().getJda();
        var channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            return;
        }
        JsonObject obj = new JsonParser().parse(respMsg.response()).getAsJsonObject();
        String componentId = obj.get("componentId").getAsString();
        String newLabel = obj.has("newLabel") ? obj.get("newLabel").getAsString() : null;
        String newStyle = obj.has("newStyle") ? obj.get("newStyle").getAsString() : null;
        Boolean disabled = obj.has("disabled") ? obj.get("disabled").getAsBoolean() : null;
        new MessageComponentFetcher(channel, messageId)
                .fetchAndApply(rows -> {
                    new ButtonEditor(componentId, newLabel, newStyle, disabled)
                            .edit(rows);
                    new MessageUpdater(channel, messageId, rows).update();
                });
    }
}
