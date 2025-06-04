package com.wairesd.discordbm.velocity.commandbuilder.models.codinations;

import com.wairesd.discordbm.velocity.commandbuilder.models.context.Context;

public interface CommandCondition {
    boolean check(Context context);
}