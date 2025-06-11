package com.wairesd.discordbm.velocity.commandbuilder.interaction.strategy;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;

public interface ResponseStrategy {
    void apply(Context context, String targetId);
}
