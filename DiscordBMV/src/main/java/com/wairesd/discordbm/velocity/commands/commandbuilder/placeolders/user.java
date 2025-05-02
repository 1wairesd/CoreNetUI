package com.wairesd.discordbm.velocity.commands.commandbuilder.placeolders;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class user {

    public static String replace(String template, SlashCommandInteractionEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        Objects.requireNonNull(template, "Template cannot be null");

        return template.replace("{user}", event.getUser().getAsTag());
    }
}
