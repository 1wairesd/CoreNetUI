package com.wairesd.discordbm.host.common.commandbuilder.interaction.response;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.strategy.ResponseStrategy;
import net.dv8tion.jda.api.interactions.InteractionHook;
import java.util.List;
import java.util.Random;

public class RandomReplyResponse implements ResponseStrategy {
    private static final Random RANDOM = new Random();

    @Override
    public void apply(Context context, String targetId) {
        List<String> messages = context.getMessageList();
        String message = (messages != null && !messages.isEmpty())
                ? messages.get(RANDOM.nextInt(messages.size()))
                : context.getMessageText();
        InteractionHook hook = context.getHook();
        if (hook != null) {
            hook.sendMessage(message).setEphemeral(context.isEphemeral()).queue();
        }
    }
} 