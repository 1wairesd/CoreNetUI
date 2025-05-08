package com.wairesd.discordbm.velocity.commands.commandbuilder.utils;

import com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders.PlaceholdersUser;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageFormatterUtils {
    private static final Logger logger = LoggerFactory.getLogger(MessageFormatterUtils.class);

    public static String format(String template, SlashCommandInteractionEvent event, Context context, boolean debugLog) {
        String result = PlaceholdersUser.replace(template != null ? template : "", event, context);
        for (OptionMapping option : event.getOptions()) {
            result = result.replace("{" + option.getName() + "}", option.getAsString());
        }
        if (debugLog) logger.info("Formatted message: {}", result);
        return result;
    }
}
