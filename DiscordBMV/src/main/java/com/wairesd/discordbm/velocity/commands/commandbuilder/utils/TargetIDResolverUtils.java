package com.wairesd.discordbm.velocity.commands.commandbuilder.utils;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.placeholders.PlaceholdersChannel;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.placeholders.PlaceholdersMessageID;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TargetIDResolverUtils {
    public static String resolve(SlashCommandInteractionEvent event, String targetId, Context context) {
        String resolved = PlaceholdersMessageID.resolveMessageId(targetId, context);
        return PlaceholdersChannel.resolveChannelId(resolved, context);
    }
}
