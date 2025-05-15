package com.wairesd.discordbm.velocity.commands.commandbuilder.strategy;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;

public interface ResponseStrategy {
    void apply(Context context, String targetId);
}
