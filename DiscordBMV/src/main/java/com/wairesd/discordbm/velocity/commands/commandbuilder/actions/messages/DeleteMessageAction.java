package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages;

import com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders.PlaceholdersResolved;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.config.configurators.Commands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DeleteMessageAction implements CommandAction {
    private final String label;
    private final String channelId;
    private final String messageId;

    public DeleteMessageAction(Map<String, Object> properties) {
        this.label = (String) properties.get("label");
        this.channelId = (String) properties.get("channel_id");
        this.messageId = (String) properties.get("message_id");

        if (this.label == null && (this.channelId == null || this.messageId == null)) {
            throw new IllegalArgumentException("Either label or both channel_id and message_id must be provided");
        }
        if (this.label != null && (this.channelId != null || this.messageId != null)) {
            throw new IllegalArgumentException("Cannot provide both label and channel_id/message_id");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        return CompletableFuture.runAsync(() -> {
            JDA jda = context.getEvent().getJDA();
            String deleteChannelId;
            String deleteMessageId;

            if (label != null) {
                String fullLabel = context.getEvent().getGuild().getId() + "_" + label;
                String[] parts = Commands.plugin.getMessageReference(fullLabel);
                if (parts == null || parts.length != 2) {
                    throw new IllegalArgumentException("No message found for label " + label);
                }
                deleteChannelId = parts[0];
                deleteMessageId = parts[1];
            } else {
                deleteChannelId = PlaceholdersResolved.replace(channelId, context);
                deleteMessageId = PlaceholdersResolved.replace(messageId, context);
            }

            TextChannel channel = jda.getTextChannelById(deleteChannelId);
            if (channel == null) {
                throw new IllegalArgumentException("Channel not found: " + deleteChannelId);
            }

            channel.deleteMessageById(deleteMessageId).queue();
        });
    }

}