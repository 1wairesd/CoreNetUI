package com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;

public class PlaceholdersChannel {
    public static String resolveChannelId(String targetId, Context context) {
        if (targetId == null) return null;

        if (targetId.equals("{channel}")) {
            return context.getEvent().getChannel().getId();
        }

        return targetId;
    }
}