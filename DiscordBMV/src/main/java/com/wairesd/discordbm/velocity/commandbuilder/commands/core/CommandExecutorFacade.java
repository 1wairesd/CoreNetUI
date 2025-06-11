package com.wairesd.discordbm.velocity.commandbuilder.commands.core;

import com.wairesd.discordbm.velocity.commandbuilder.commands.executor.CommandExecutor;
import com.wairesd.discordbm.velocity.commandbuilder.core.models.structures.CommandStructured;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandExecutorFacade {
    private final CommandExecutor executor = new CommandExecutor();

    public void execute(SlashCommandInteractionEvent event, CommandStructured command) {
        executor.execute(event, command);
    }
}
