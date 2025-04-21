package com.wairesd.discordbm.velocity.commands.custom;

import com.wairesd.discordbm.velocity.commands.custom.models.CustomCommand;
import com.wairesd.discordbm.velocity.config.configurators.Commands;
import com.wairesd.discordbm.velocity.models.command.CommandDefinition;
import com.wairesd.discordbm.velocity.models.option.OptionDefinition;
import com.wairesd.discordbm.velocity.network.NettyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

public class CommandManager {
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private final Map<String, CustomCommand> customCommands = new HashMap<>();
    private final NettyServer nettyServer;
    private final JDA jda;

    public CommandManager(NettyServer nettyServer, JDA jda) {
        this.nettyServer = Objects.requireNonNull(nettyServer, "NettyServer cannot be null");
        this.jda = Objects.requireNonNull(jda, "JDA cannot be null");
    }

    public void loadAndRegisterCommands() {
        customCommands.clear();
        Commands.getCustomCommands().stream()
                .filter(Objects::nonNull)
                .forEach(this::loadAndRegisterCommand);
        logger.info("Loaded and registered {} custom commands", customCommands.size());
    }

    private void loadAndRegisterCommand(CustomCommand cmd) {
        customCommands.put(cmd.getName(), cmd);
        registerCommandWithJDA(cmd);
        registerCommandDefinition(cmd);
    }

    private void registerCommandWithJDA(CustomCommand cmd) {
        SlashCommandData cmdData = createSlashCommandData(cmd);
        jda.upsertCommand(cmdData).queue();
    }

    private SlashCommandData createSlashCommandData(CustomCommand cmd) {
        SlashCommandData cmdData = slash(cmd.getName(), cmd.getDescription());
        addOptionsToCommand(cmd, cmdData);
        setCommandContext(cmd, cmdData);
        return cmdData;
    }


    private void addOptionsToCommand(CustomCommand cmd, SlashCommandData cmdData) {
        cmd.getOptions().forEach(opt -> {
            try {
                OptionType optionType = OptionType.valueOf(opt.getType().toUpperCase());
                cmdData.addOption(optionType, opt.getName(), opt.getDescription(), opt.isRequired());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid option type '{}' for command '{}'", opt.getType(), cmd.getName());
            }
        });
    }

    private void setCommandContext(CustomCommand cmd, SlashCommandData cmdData) {
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

    private void registerCommandDefinition(CustomCommand cmd) {
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

    public CustomCommand getCommand(String name) {
        return customCommands.get(name);
    }
}
