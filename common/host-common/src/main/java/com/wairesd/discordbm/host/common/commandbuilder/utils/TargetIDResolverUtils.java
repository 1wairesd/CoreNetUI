package com.wairesd.discordbm.host.common.commandbuilder.utils;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders.PlaceholdersChannel;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders.PlaceholdersMessageID;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders.PlaceholdersOption;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TargetIDResolverUtils {
    public static String resolve(SlashCommandInteractionEvent event, String targetId, Context context) {
        if (targetId == null) return null;
        
        if ("{channel}".equals(targetId)) {
            String channelId = PlaceholdersOption.resolveOption(targetId, event);
            if (channelId != null) {
                return channelId;
            }
        }
        
        String resolved = PlaceholdersMessageID.resolveMessageId(targetId, context);
        return PlaceholdersChannel.resolveChannelId(resolved, context);
    }
}
