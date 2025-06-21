package com.wairesd.discordbm.host.common.commandbuilder.utils;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;

public class ContextUtils {
    public static void validate(Context context) {
        if (context == null || context.getEvent() == null) {
            throw new NullPointerException("Context or event cannot be null");
        }
    }
}
