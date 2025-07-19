package com.wairesd.discordbm.host.common.commandbuilder.commands.processor;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.commandbuilder.core.parser.CommandParserCondition;
import com.wairesd.discordbm.host.common.models.command.CommandDefinition;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.conditions.CommandCondition;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommandLoader {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));

    public List<CommandStructured> load() {
        try {
            List<CommandStructured> commands = Commands.getCustomCommands();
            if (Settings.isDebugCommandRegistrations()) {
                logger.info("Loaded commands:");
                commands.forEach(cmd -> logger.info(" - {}", cmd.getName()));
            }
            return commands;
        } catch (Exception e) {
            logger.error("Failed to load commands", e);
            return List.of();
        }
    }

    public List<CommandStructured> loadFromDefinitions(List<CommandDefinition> definitions) {
        return definitions.stream().map(def -> {
            List<CommandCondition> conditions = def.conditions() != null ?
                def.conditions().stream().map(CommandParserCondition::parseCondition).toList() : List.of();
            return new CommandStructured(
                def.name(),
                def.description(),
                def.context(),
                List.of(),
                conditions,
                List.of(),
                List.of(),
                null,
                def.permission(),
                def.pluginName()
            );
        }).toList();
    }
}
