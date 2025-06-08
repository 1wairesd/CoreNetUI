package com.wairesd.discordbm.velocity.commandbuilder.models.conditions;

import com.wairesd.discordbm.velocity.commandbuilder.models.context.Context;

public interface CommandCondition {
    boolean check(Context context);
}