package com.wairesd.discordbm.host.common.commandbuilder.commands.core;

import com.wairesd.discordbm.host.common.commandbuilder.commands.executor.CommandExecutor;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandExecutorFacade {
    private final CommandExecutor executor = new CommandExecutor();

    public void execute(SlashCommandInteractionEvent event, CommandStructured command) {
        executor.execute(event, command);
    }
}
