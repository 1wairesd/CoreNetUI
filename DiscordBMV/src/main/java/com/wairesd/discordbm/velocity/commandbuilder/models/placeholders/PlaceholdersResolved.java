package com.wairesd.discordbm.velocity.commandbuilder.models.placeholders;

import com.wairesd.discordbm.velocity.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.concurrent.CompletableFuture;

public class PlaceholdersResolved implements Placeholder {
    @Override
    public CompletableFuture<String> replace(String template, Interaction event, Context context) {
        if (template == null) return CompletableFuture.completedFuture("");
        if (context != null && template.contains("{resolved_message}")) {
            String resolved = context.getResolvedMessage();
            String result = template.replace("{resolved_message}", resolved != null ? resolved : "");
            return CompletableFuture.completedFuture(result);
        }
        return CompletableFuture.completedFuture(template);
    }

    public static String replaceSync(String template, Context context) {
        if (template == null) return "";
        if (context != null && template.contains("{resolved_message}")) {
            String resolved = context.getResolvedMessage();
            return template.replace("{resolved_message}", resolved != null ? resolved : "");
        }
        return template;
    }
}
