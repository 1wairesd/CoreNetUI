package com.wairesd.discordbm.velocity.commands.commandbuilder;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.structures.CommandStructured;
import com.wairesd.discordbm.velocity.config.configurators.Commands;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import com.wairesd.discordbm.velocity.models.command.CommandDefinition;
import com.wairesd.discordbm.velocity.models.option.OptionDefinition;
import com.wairesd.discordbm.velocity.network.NettyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

public class CommandManager {
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private final Map<String, CommandStructured> customCommands = new HashMap<>();
    private final NettyServer nettyServer;
    private final JDA jda;

    public CommandManager(NettyServer nettyServer, JDA jda) {
        this.nettyServer = Objects.requireNonNull(nettyServer, "NettyServer cannot be null");
        this.jda = Objects.requireNonNull(jda, "JDA cannot be null");
    }

    public void loadAndRegisterCommands() {
        List<CommandStructured> commands;
        try {
            commands = Commands.getCustomCommands();
            if (Settings.isDebugCommandRegistrations()) {
                logger.debug("Loading custom commands from configuration...");
                commands.forEach(cmd -> logger.debug("Loaded command: {}", cmd.getName()));
            }
            logger.info("Successfully loaded {} commands", commands.size());
        } catch (Exception e) {
            logger.error("Failed to load custom commands", e);
            return;
        }

        customCommands.clear();
        int registered = 0;
        for (CommandStructured cmd : commands) {
            if (cmd == null) continue;
            if (safeRegisterCommand(cmd)) registered++;
        }

        logger.info("Completed registration of {} commands", registered);
        if (Settings.isDebugCommandRegistrations()) {
            logger.debug("Registered commands: {}", customCommands.keySet());
        }
    }

    private boolean safeRegisterCommand(CommandStructured cmd) {
        try {
            SlashCommandData cmdData = createSlashCommandData(cmd);
            if (Settings.isDebugCommandRegistrations()) {
                logger.debug("Registering command '{}' with options: {}", cmd.getName(), cmd.getOptions());
            }

            jda.upsertCommand(cmdData).queue();
            registerCommandDefinition(cmd);
            customCommands.put(cmd.getName(), cmd);
            return true;
        } catch (Exception e) {
            logger.error("Command registration failed for '{}'", cmd.getName(), e);
            return false;
        }
    }

    private SlashCommandData createSlashCommandData(CommandStructured cmd) {
        SlashCommandData cmdData = slash(cmd.getName(), cmd.getDescription());
        addOptionsToCommand(cmd, cmdData);
        setCommandContext(cmd, cmdData);
        return cmdData;
    }

    private void addOptionsToCommand(CommandStructured cmd, SlashCommandData cmdData) {
        cmd.getOptions().forEach(opt -> {
            try {
                OptionType optionType = OptionType.valueOf(opt.getType().toUpperCase());
                cmdData.addOption(optionType, opt.getName(), opt.getDescription(), opt.isRequired());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid option type '{}' in command '{}'", opt.getType(), cmd.getName());
            }
        });
    }

    private void setCommandContext(CommandStructured cmd, SlashCommandData cmdData) {
        boolean guildOnly = switch (cmd.getContext()) {
            case "server" -> true;
            case "dm", "both" -> false;
            default -> {
                logger.warn("Unknown context '{}' for command '{}'. Defaulting to 'both'.", cmd.getContext(), cmd.getName());
                yield false;
            }
        };
        cmdData.setGuildOnly(guildOnly);
    }

    private void registerCommandDefinition(CommandStructured cmd) {
        CommandDefinition def = new CommandDefinition(
                cmd.getName(),
                cmd.getDescription(),
                cmd.getContext(),
                cmd.getOptions().stream()
                        .map(opt -> new OptionDefinition(
                                opt.getName(),
                                opt.getType(),
                                opt.getDescription(),
                                opt.isRequired()
                        ))
                        .toList()
        );
        nettyServer.getCommandDefinitions().put(cmd.getName(), def);
    }

    public CommandStructured getCommand(String name) {
        return customCommands.get(name);
    }
}