package com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;

import java.util.concurrent.CompletableFuture;

public interface CommandAction {
    CompletableFuture<Void> execute(Context context);
}