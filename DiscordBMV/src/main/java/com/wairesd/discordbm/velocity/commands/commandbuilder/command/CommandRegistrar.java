package com.wairesd.discordbm.velocity.commands.commandbuilder.command;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.structures.CommandStructured;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRegistrar {
    private final JDA jda;
    private final CommandBuilder builder;
    private final Logger logger = LoggerFactory.getLogger(CommandRegistrar.class);

    public CommandRegistrar(JDA jda, CommandBuilder builder) {
        this.jda = jda;
        this.builder = builder;
    }

    public boolean register(CommandStructured cmd) {
        try {
            SlashCommandData data = builder.build(cmd);
            jda.upsertCommand(data).queue();
            return true;
        } catch (Exception e) {
            logger.error("Failed to register command '{}'", cmd.getName(), e);
            return false;
        }
    }
}
