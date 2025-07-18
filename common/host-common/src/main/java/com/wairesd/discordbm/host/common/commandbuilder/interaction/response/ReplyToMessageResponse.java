package com.wairesd.discordbm.host.common.commandbuilder.interaction.response;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.strategy.ResponseStrategy;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ReplyToMessageResponse implements ResponseStrategy {
    @Override
    public void apply(Context context, String targetId) {
        String message = context.getMessageText();
        String replyMessageId = context.getVariables() != null ? context.getVariables().get("reply_message_id") : null;
        if (replyMessageId == null || replyMessageId.isEmpty()) {
            replyMessageId = context.getOption("message_id");
        }
        Boolean mentionAuthor = Boolean.TRUE.equals(context.getVariables() != null ? context.getVariables().get("reply_mention_author") : false);
        if (context.getEvent() instanceof SlashCommandInteractionEvent event && replyMessageId != null && !replyMessageId.isEmpty()) {
            event.deferReply(true).queue(hook -> hook.deleteOriginal().queue());
            var msgAction = event.getChannel().sendMessage(message)
                .setMessageReference(replyMessageId)
                .mentionRepliedUser(mentionAuthor);
            if (context.getEmbed() != null) msgAction.setEmbeds(context.getEmbed());
            if (!context.getActionRows().isEmpty()) msgAction.setComponents(context.getActionRows());
            msgAction.queue();
        }
    }
} 