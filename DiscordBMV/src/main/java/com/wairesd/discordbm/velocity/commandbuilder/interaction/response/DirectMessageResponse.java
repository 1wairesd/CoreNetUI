package com.wairesd.discordbm.velocity.commandbuilder.interaction.response;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.velocity.commandbuilder.interaction.strategy.ResponseStrategy;

public class DirectMessageResponse implements ResponseStrategy {
    public void apply(Context context, String targetId) {
        context.setTargetUserId(targetId);
    }
}