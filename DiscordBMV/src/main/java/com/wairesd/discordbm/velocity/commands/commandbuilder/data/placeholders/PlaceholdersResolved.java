package com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;

public class PlaceholdersResolved {
    public static String replace(String template, Context context) {
        if (template == null) return "";

        if (context != null && template.contains("{resolved_message}")) {
            String resolved = context.getResolvedMessage();
            template = template.replace("{resolved_message}", resolved != null ? resolved : "");
        }

        return template;
    }
}
