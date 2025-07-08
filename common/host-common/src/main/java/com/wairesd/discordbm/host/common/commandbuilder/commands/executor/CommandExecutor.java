package com.wairesd.discordbm.host.common.commandbuilder.commands.executor;

import com.wairesd.discordbm.host.common.commandbuilder.commands.processor.CommandProcessor;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.CommandResponder;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.validator.CommandValidator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandExecutor {
    private final CommandValidator validator = new CommandValidator();
    private final CommandProcessor processor = new CommandProcessor();
    private final CommandResponder responder = new CommandResponder();

    public void execute(SlashCommandInteractionEvent event, CommandStructured command) {
        Context context = new Context(event);

        if (!validator.validateConditions(command, context)) {
            responder.handleFailedValidation(event, command, context);
            return;
        }

        processor.process(command, context, event, responder);
    }
}
