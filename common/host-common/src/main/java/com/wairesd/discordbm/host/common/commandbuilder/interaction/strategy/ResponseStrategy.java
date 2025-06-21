package com.wairesd.discordbm.host.common.commandbuilder.interaction.strategy;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;

public interface ResponseStrategy {
    void apply(Context context, String targetId);
}
