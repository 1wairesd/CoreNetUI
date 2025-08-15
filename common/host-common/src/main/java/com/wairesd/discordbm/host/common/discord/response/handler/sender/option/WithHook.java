package com.wairesd.discordbm.host.common.discord.response.handler.sender.option;

import com.wairesd.discordbm.common.models.buttons.ButtonStyle;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.host.common.utils.Components;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class WithHook {

    public static void sendResponseWithHook(InteractionHook hook, ResponseMessage respMsg) {
        boolean ephemeral = false;

        if (respMsg.embed() != null) {
            sendEmbed(hook, respMsg, ephemeral);
        } else if (respMsg.response() != null) {
            sendText(hook, respMsg, ephemeral);
        }
    }

    private static void sendEmbed(InteractionHook hook, ResponseMessage respMsg, boolean ephemeral) {
        var embed = buildEmbed(respMsg);

        if (respMsg.buttons() != null && !respMsg.buttons().isEmpty()) {
            List<Button> jdaButtons = buildButtons(respMsg);
            sendEmbedWithButtons(hook, embed, jdaButtons, ephemeral);
        } else {
            sendEmbedWithoutButtons(hook, embed, ephemeral);
        }
    }

    private static EmbedBuilder buildEmbedBuilder(ResponseMessage respMsg) {
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
            respMsg.embed().fields().forEach(
                    field -> embedBuilder.addField(field.name(), field.value(), field.inline()));
        }
        return embedBuilder;
    }

    private static net.dv8tion.jda.api.entities.MessageEmbed buildEmbed(ResponseMessage respMsg) {
        return buildEmbedBuilder(respMsg).build();
    }

    private static List<Button> buildButtons(ResponseMessage respMsg) {
        return respMsg.buttons().stream()
                .map(
                        btn -> {
                            if (btn.style() == ButtonStyle.LINK) {
                                return Button.link(btn.url(), btn.label());
                            } else {
                                return Button.of(Components.getJdaButtonStyle(btn.style()), btn.customId(), btn.label())
                                        .withDisabled(btn.disabled());
                            }
                        })
                .collect(Collectors.toList());
    }

    private static void sendEmbedWithButtons(
            InteractionHook hook, MessageEmbed embed, List<Button> buttons, boolean ephemeral) {
        if (ephemeral) {
            hook.sendMessageEmbeds(embed).addActionRow(buttons).setEphemeral(true).queue();
        } else {
            hook.editOriginalEmbeds(embed).setActionRow(buttons.toArray(new Button[0])).queue();
        }
    }

    private static void sendEmbedWithoutButtons(InteractionHook hook, MessageEmbed embed, boolean ephemeral) {
        if (ephemeral) {
            hook.sendMessageEmbeds(embed).setEphemeral(true).queue();
        } else {
            hook.editOriginalEmbeds(embed).queue();
        }
    }

    private static void sendText(InteractionHook hook, ResponseMessage respMsg, boolean ephemeral) {
        if (ephemeral) {
            hook.sendMessage(respMsg.response()).setEphemeral(true).queue();
        } else {
            hook.editOriginal(respMsg.response()).queue();
        }
    }
}
