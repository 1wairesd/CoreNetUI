package com.wairesd.discordbm.velocity.commands.models;

import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.models.command.CommandDefinition;
import io.netty.channel.Channel;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(CommandRegistrationService.class);

    private final Object jda;
    private final Map<String, CommandDefinition> commandDefinitions = new HashMap<>();
    private final Map<String, List<ServerInfo>> commandToServers = new HashMap<>();

    public CommandRegistrationService(Object jda) {
        this.jda = jda;
    }

    public void registerCommands(String serverName, List<CommandDefinition> commands, Channel channel) {
        if (jda == null) {
            logger.warn("Cannot register commands - JDA is not initialized!");
            return;
        }

        for (var cmd : commands) {
            if (commandDefinitions.containsKey(cmd.name())) {
                CommandDefinition existing = commandDefinitions.get(cmd.name());
                if (!existing.equals(cmd)) {
                    if (Settings.isDebugErrors()) {
                        logger.error("Command {} from server {} has different definition", cmd.name(), serverName);
                    }
                    continue;
                }
            } else {
                commandDefinitions.put(cmd.name(), cmd);

                var cmdData = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(cmd.name(), cmd.description());

                for (var opt : cmd.options()) {
                    cmdData.addOption(
                            net.dv8tion.jda.api.interactions.commands.OptionType.valueOf(opt.type()),
                            opt.name(),
                            opt.description(),
                            opt.required()
                    );
                }

                switch (cmd.context()) {
                    case "both", "dm" -> cmdData.setGuildOnly(false);
                    case "server" -> cmdData.setGuildOnly(true);
                    default -> {
                        if (Settings.isDebugErrors()) {
                            logger.warn("Unknown context '{}' for command '{}'. Defaulting to 'both'.", cmd.context(), cmd.name());
                        }
                        cmdData.setGuildOnly(false);
                    }
                }

                ((JDA) jda).upsertCommand(cmdData).queue();

                if (Settings.isDebugCommandRegistrations()) {
                    logger.info("Registered command: {} with context: {}", cmd.name(), cmd.context());
                }
            }

            List<ServerInfo> servers = commandToServers.computeIfAbsent(cmd.name(), k -> new ArrayList<>());
            servers.removeIf(serverInfo -> serverInfo.serverName().equals(serverName));
            servers.add(new ServerInfo(serverName, channel));
        }
    }

    public record ServerInfo(String serverName, Channel channel) {}
}
