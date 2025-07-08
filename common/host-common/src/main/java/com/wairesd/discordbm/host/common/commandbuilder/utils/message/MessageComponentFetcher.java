package com.wairesd.discordbm.host.common.commandbuilder.utils.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;
import java.util.function.Consumer;

public class MessageComponentFetcher {
    private final TextChannel channel;
    private final String messageId;

    public MessageComponentFetcher(TextChannel channel, String messageId) {
        this.channel = channel;
        this.messageId = messageId;
    }

    public void fetchAndApply(Consumer<List<ActionRow>> consumer) {
        channel.retrieveMessageById(messageId).queue(
                (Message message) -> consumer.accept(message.getActionRows()),
                throwable -> {}
        );
    }
}
