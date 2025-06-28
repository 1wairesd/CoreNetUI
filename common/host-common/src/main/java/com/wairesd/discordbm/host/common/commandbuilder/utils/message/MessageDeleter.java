package com.wairesd.discordbm.host.common.commandbuilder.utils.message;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MessageDeleter {

    public void deleteMessage(TextChannel channel, String messageId) {
        channel.deleteMessageById(messageId).queue();
    }
}
