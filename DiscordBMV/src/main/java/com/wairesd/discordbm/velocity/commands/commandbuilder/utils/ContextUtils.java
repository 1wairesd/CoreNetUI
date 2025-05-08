package com.wairesd.discordbm.velocity.commands.commandbuilder.utils;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;

public class ContextUtils {
    public static void validate(Context context) {
        if (context == null || context.getEvent() == null) {
            throw new NullPointerException("Context or event cannot be null");
        }
    }
}
