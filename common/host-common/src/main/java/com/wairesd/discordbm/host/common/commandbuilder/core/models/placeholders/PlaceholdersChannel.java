package com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.concurrent.CompletableFuture;

public class PlaceholdersChannel implements Placeholder {

    @Override
    public CompletableFuture<String> replace(String template, Interaction event, Context context) {
        String result = template
                .replace("{channel_id}", event.getChannel().getId())
                .replace("{channel_name}", event.getChannel().getName())
                .replace("{channel}", "#" + event.getChannel().getName());

        return CompletableFuture.completedFuture(result);
    }

    public static String resolveChannelId(String targetId, Context context) {
        if (targetId == null) return null;

        if (targetId.equals("{channel}")) {
            return context.getEvent().getChannel().getId();
        }

        if (context.getEvent() instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent slashEvent = (SlashCommandInteractionEvent) context.getEvent();

            if (targetId.startsWith("{") && targetId.endsWith("}")) {
                String optionName = targetId.substring(1, targetId.length() - 1);
                if (optionName.equals("channel")) {
                    OptionMapping channelOption = slashEvent.getOption("channel");
                    if (channelOption != null) {
                        Channel channel = channelOption.getAsChannel();
                        if (channel != null) {
                            return channel.getId();
                        }
                    }
                }
            }
        }
        
        return targetId;
    }
}
