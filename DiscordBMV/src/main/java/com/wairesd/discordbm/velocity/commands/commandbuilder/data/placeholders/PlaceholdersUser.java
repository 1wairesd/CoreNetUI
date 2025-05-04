package com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PlaceholdersUser {
    public static String replace(String template, SlashCommandInteractionEvent event, Context context) {
        template = PlaceholdersResolved.replace(template, context);

        return template
                .replace("{user}", event.getUser().getAsTag())
                .replace("{user_id}", event.getUser().getId())
                .replace("{user_name}", event.getUser().getName());
    }
}
