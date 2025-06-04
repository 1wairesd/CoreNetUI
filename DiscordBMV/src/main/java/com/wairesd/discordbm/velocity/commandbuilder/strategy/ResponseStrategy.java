package com.wairesd.discordbm.velocity.commandbuilder.strategy;

import com.wairesd.discordbm.velocity.commandbuilder.models.context.Context;

public interface ResponseStrategy {
    void apply(Context context, String targetId);
}
