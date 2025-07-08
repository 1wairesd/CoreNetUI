package com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.concurrent.CompletableFuture;

public interface Placeholder {
    CompletableFuture<String> replace(String template, Interaction event, Context context);
}