package com.wairesd.discordbm.velocity.commands.commandbuilder.command;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.structures.CommandStructured;
import com.wairesd.discordbm.velocity.config.configurators.Commands;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommandLoader {
    private static final Logger logger = LoggerFactory.getLogger(CommandLoader.class);

    public List<CommandStructured> load() {
        try {
            List<CommandStructured> commands = Commands.getCustomCommands();
            if (Settings.isDebugCommandRegistrations()) {
                logger.debug("Loaded commands:");
                commands.forEach(cmd -> logger.debug(" - {}", cmd.getName()));
            }
            return commands;
        } catch (Exception e) {
            logger.error("Failed to load commands", e);
            return List.of();
        }
    }
}
