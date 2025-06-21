package com.wairesd.discordbm.host.common.commandbuilder.interaction.response;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.strategy.ResponseStrategy;

public class EditMessageResponse implements ResponseStrategy {
    public void apply(Context context, String targetId) {
        context.setMessageIdToEdit(targetId);
    }
}