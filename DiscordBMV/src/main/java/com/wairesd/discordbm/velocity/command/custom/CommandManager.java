package com.wairesd.discordbm.velocity.command.custom;

import com.wairesd.discordbm.velocity.command.custom.models.CustomCommand;
import com.wairesd.discordbm.velocity.model.CommandDefinition;
import com.wairesd.discordbm.velocity.model.OptionDefinition;
import com.wairesd.discordbm.velocity.network.NettyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import com.wairesd.discordbm.velocity.config.configurators.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private final Map<String, CustomCommand> customCommands = new HashMap<>();
    private final NettyServer nettyServer;
    private final JDA jda;

    public CommandManager(NettyServer nettyServer, JDA jda) {
        if (nettyServer == null || jda == null) {
            throw new IllegalArgumentException("NettyServer and JDA cannot be null");
        }
        this.nettyServer = nettyServer;
        this.jda = jda;
    }

    public void loadAndRegisterCommands() {
        customCommands.clear();
        for (CustomCommand cmd : Commands.getCustomCommands()) {
            if (cmd == null) {
                logger.warn("Skipping null command in configuration");
                continue;
            }
            customCommands.put(cmd.getName(), cmd);
            registerCommand(cmd);
        }
        logger.info("Loaded and registered {} custom commands", customCommands.size());
    }

    private void registerCommand(CustomCommand cmd) {
        var cmdData = net.dv8tion.jda.api.interactions.commands.build.Commands
                .slash(cmd.getName(), cmd.getDescription());
        addOptionsToCommand(cmd, cmdData);
        setCommandContext(cmd, cmdData);
        jda.upsertCommand(cmdData).queue();
        registerCommandDefinition(cmd);
    }

    private void addOptionsToCommand(CustomCommand cmd, SlashCommandData cmdData) {
        for (var opt : cmd.getOptions()) {
            try {
                OptionType optionType = OptionType.valueOf(opt.getType());
                cmdData.addOption(optionType, opt.getName(), opt.getDescription(), opt.isRequired());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid option type '{}' for command '{}'", opt.getType(), cmd.getName());
            }
        }
    }

    private void setCommandContext(CustomCommand cmd, SlashCommandData cmdData) {
        switch (cmd.getContext()) {
            case "both":
            case "dm":
                cmdData.setGuildOnly(false);
                break;
            case "server":
                cmdData.setGuildOnly(true);
                break;
            default:
                logger.warn("Unknown context '{}' for command '{}'. Defaulting to 'both'.", cmd.getContext(), cmd.getName());
                cmdData.setGuildOnly(false);
        }
    }

    private void registerCommandDefinition(CustomCommand cmd) {
        CommandDefinition def = new CommandDefinition(
                cmd.getName(),
                cmd.getDescription(),
                cmd.getContext(),
                cmd.getOptions().stream()
                        .map(opt -> new OptionDefinition(opt.getName(), opt.getType(), opt.getDescription(), opt.isRequired()))
                        .toList()
        );
        nettyServer.getCommandDefinitions().put(cmd.getName(), def);
    }

    public CustomCommand getCommand(String name) {
        return customCommands.get(name);
    }
}