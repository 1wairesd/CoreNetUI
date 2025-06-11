package com.wairesd.discordbm.velocity.commandbuilder.core.models.actions;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;

import java.util.concurrent.CompletableFuture;

public interface CommandAction {
    CompletableFuture<Void> execute(Context context);
}