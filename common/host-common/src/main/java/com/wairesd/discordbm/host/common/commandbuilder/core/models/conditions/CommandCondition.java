package com.wairesd.discordbm.host.common.commandbuilder.core.models.conditions;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;

public interface CommandCondition {
    boolean check(Context context);
}