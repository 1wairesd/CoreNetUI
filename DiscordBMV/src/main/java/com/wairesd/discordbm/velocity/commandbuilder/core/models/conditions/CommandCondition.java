package com.wairesd.discordbm.velocity.commandbuilder.core.models.conditions;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;

public interface CommandCondition {
    boolean check(Context context);
}