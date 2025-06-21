package com.wairesd.discordbm.host.common.commandbuilder.utils.message;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MessageDeleter {
    /**
     * Удаляет одно сообщение по его ID
     * 
     * @param channel Текстовый канал, где находится сообщение
     * @param messageId ID сообщения для удаления
     */
    public void deleteMessage(TextChannel channel, String messageId) {
        channel.deleteMessageById(messageId).queue();
    }
}
