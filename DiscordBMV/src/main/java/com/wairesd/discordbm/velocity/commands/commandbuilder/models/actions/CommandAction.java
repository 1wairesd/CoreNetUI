package com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;

public interface CommandAction {
    void execute(Context context);
}