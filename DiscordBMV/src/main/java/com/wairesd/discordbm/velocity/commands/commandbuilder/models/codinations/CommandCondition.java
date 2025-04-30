package com.wairesd.discordbm.velocity.commands.commandbuilder.models.codinations;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;

public interface CommandCondition {
    boolean check(Context context);
}