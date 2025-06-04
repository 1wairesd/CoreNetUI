package com.wairesd.discordbm.velocity.commandbuilder.responses;

import com.wairesd.discordbm.velocity.commandbuilder.models.context.Context;
import com.wairesd.discordbm.velocity.commandbuilder.strategy.ResponseStrategy;

public class DirectMessageResponse implements ResponseStrategy {
    public void apply(Context context, String targetId) {
        context.setTargetUserId(targetId);
    }
}