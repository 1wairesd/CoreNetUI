package com.wairesd.discordbm.host.common.discord.response.handler.editor.option;

import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.utils.Components;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.stream.Collectors;

public class EditMessage {
    private static DiscordBMHPlatformManager platformManager;

    public static void editMessage(ResponseMessage respMsg) {
        String label = respMsg.requestId();
        if (respMsg.type() != null && respMsg.type().equals("edit_message")) {
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
        if (respMsg.embed() != null || (respMsg.buttons() != null && !respMsg.buttons().isEmpty())) {
            var action = channel.editMessageById(messageId, respMsg.response() != null ? respMsg.response() : "");
            if (respMsg.embed() != null) {
                var embed = Components.toJdaEmbed(respMsg.embed()).build();
                action = action.setEmbeds(embed);
            }
            if (respMsg.buttons() != null && !respMsg.buttons().isEmpty()) {
                var jdaButtons = respMsg.buttons().stream()
                        .map(btn -> Button.of(Components.getJdaButtonStyle(btn.style()), btn.customId(), btn.label()))
                        .collect(Collectors.toList());
                action = action.setActionRow(jdaButtons);
            }
            action.queue();
        } else {
            channel.editMessageById(messageId, respMsg.response() != null ? respMsg.response() : "").queue();
        }
    }
}
