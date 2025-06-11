package com.wairesd.discordbm.velocity.commandbuilder.interaction.validator;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;

public class CommandValidator {
    public boolean validateConditions(CommandStructured command, Context context) {
        return command.getConditions().stream().allMatch(c -> c.check(context));
    }
}
