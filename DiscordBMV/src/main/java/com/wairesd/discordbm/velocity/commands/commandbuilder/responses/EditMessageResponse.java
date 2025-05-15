package com.wairesd.discordbm.velocity.commands.commandbuilder.responses;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.commands.commandbuilder.strategy.ResponseStrategy;

public class EditMessageResponse implements ResponseStrategy {
    public void apply(Context context, String targetId) {
        context.setMessageIdToEdit(targetId);
    }
}