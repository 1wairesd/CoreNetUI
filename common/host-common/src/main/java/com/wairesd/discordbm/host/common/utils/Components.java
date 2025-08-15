package com.wairesd.discordbm.host.common.utils;

import com.wairesd.discordbm.common.models.buttons.ButtonStyle;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class Components {

    public static net.dv8tion.jda.api.EmbedBuilder toJdaEmbed(EmbedDefinition embedDef) {
        var embedBuilder = new EmbedBuilder();
        if (embedDef.title() != null) embedBuilder.setTitle(embedDef.title());
        if (embedDef.description() != null) embedBuilder.setDescription(embedDef.description());
        if (embedDef.color() != null) embedBuilder.setColor(new Color(embedDef.color()));
        if (embedDef.fields() != null) {
            for (var field : embedDef.fields()) {
                embedBuilder.addField(field.name(), field.value(), field.inline());
            }
        }
        return embedBuilder;
    }

    public static net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle getJdaButtonStyle(ButtonStyle style) {
        return switch (style) {
            case PRIMARY -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.PRIMARY;
            case SECONDARY -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.SECONDARY;
            case SUCCESS -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.SUCCESS;
            case DANGER -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.DANGER;
            case LINK -> net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.LINK;
        };
    }
}
