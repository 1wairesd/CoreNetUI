package com.wairesd.discordbm.velocity.commands.commandbuilder.models.placeholders;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.interactions.Interaction;
import java.util.concurrent.CompletableFuture;

public interface Placeholder {
    CompletableFuture<String> replace(String template, Interaction event, Context context);
}