package com.wairesd.discordbm.host.common.commandbuilder.interaction.validator;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;

public class CommandValidator {
    public boolean validateConditions(CommandStructured command, Context context) {
        return command.getConditions().stream().allMatch(c -> c.check(context));
    }
}
