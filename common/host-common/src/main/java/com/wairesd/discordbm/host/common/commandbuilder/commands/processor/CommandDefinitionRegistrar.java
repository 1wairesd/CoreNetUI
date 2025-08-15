package com.wairesd.discordbm.host.common.commandbuilder.commands.processor;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.models.command.CommandDefinition;
import com.wairesd.discordbm.host.common.models.option.OptionDefinition;
import com.wairesd.discordbm.host.common.network.NettyServer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Handles registration of command definitions to the Netty server. */
public class CommandDefinitionRegistrar {

    private final NettyServer nettyServer;

    public CommandDefinitionRegistrar(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    /** Registers a structured command to the Netty server. */
    public void register(CommandStructured cmd) {
        CommandDefinition commandDefinition = buildCommandDefinition(cmd);
        nettyServer.getCommandDefinitions().put(cmd.getName(), commandDefinition);
    }

    /** Builds a CommandDefinition object from a CommandStructured object. */
    private CommandDefinition buildCommandDefinition(CommandStructured cmd) {
        return new CommandDefinition(
                cmd.getName(),
                cmd.getDescription(),
                cmd.getContext(),
                buildOptionDefinitions(cmd),
                cmd.getPermission(),
                buildConditions(cmd),
                cmd.getPluginName()
        );
    }

    /** Converts structured options to OptionDefinition objects. */
    private List<OptionDefinition> buildOptionDefinitions(CommandStructured cmd) {
        return cmd.getOptions().stream()
                .map(opt -> new OptionDefinition(
                        opt.getName(),
                        opt.getType(),
                        opt.getDescription(),
                        opt.isRequired()))
                .collect(Collectors.toList());
    }

    /** Processes conditions from CommandStructured, ensuring proper casting. */
    private List<Map<String, Object>> buildConditions(CommandStructured cmd) {
        if (cmd.getConditions() == null) {
            return List.of();
        }
        return cmd.getConditions().stream()
                .map(c -> c instanceof Map ? (Map<String, Object>) c : null)
                .collect(Collectors.toList());
    }
}
