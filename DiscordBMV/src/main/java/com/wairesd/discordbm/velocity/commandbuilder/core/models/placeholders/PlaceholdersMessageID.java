package com.wairesd.discordbm.velocity.commandbuilder.core.models.placeholders;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.velocity.config.configurators.Commands;

public class PlaceholdersMessageID {
    public static String resolveMessageId(String targetId, Context context) {
        if (targetId == null || !targetId.equals("{message_id_from_previous_command}")) {
            return targetId;
        }

        String guildId = context.getEvent().getGuild().getId();
        String fullLabel = guildId + "_welcome_message";
        String messageId = Commands.plugin.getGlobalMessageLabel(fullLabel);
        if (messageId == null) {
            throw new IllegalArgumentException("No previous message ID found for {message_id_from_previous_command}");
        }
        return messageId;
    }
}