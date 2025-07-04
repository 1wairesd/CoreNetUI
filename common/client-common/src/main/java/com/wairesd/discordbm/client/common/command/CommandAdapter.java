package com.wairesd.discordbm.client.common.command;

import com.wairesd.discordbm.api.command.Command;
import com.wairesd.discordbm.api.command.CommandOption;
import com.wairesd.discordbm.api.command.CommandCondition;
import com.wairesd.discordbm.client.common.models.command.CommandOptions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;

public class CommandAdapter implements Command {
    
    private final Command apiCommand;
    private com.wairesd.discordbm.client.common.models.command.Command internalCommand;

    public CommandAdapter(Command apiCommand) {
        this.apiCommand = apiCommand;
        this.internalCommand = convertToInternalCommand(apiCommand);
    }
    
    @Override
    public String getName() {
        return apiCommand.getName();
    }
    
    @Override
    public String getDescription() {
        return apiCommand.getDescription();
    }
    
    @Override
    public String getPluginName() {
        return apiCommand.getPluginName();
    }
    
    @Override
    public String getContext() {
        return apiCommand.getContext();
    }
    
    @Override
    public List<CommandOption> getOptions() {
        return apiCommand.getOptions();
    }

    @Override
    public List<CommandCondition> getConditions() {
        return apiCommand.getConditions();
    }

    public com.wairesd.discordbm.client.common.models.command.Command getInternalCommand() {
        return internalCommand;
    }

    private com.wairesd.discordbm.client.common.models.command.Command convertToInternalCommand(Command apiCommand) {
        List<CommandOptions> options = apiCommand.getOptions().stream()
            .map(this::convertToInternalOption)
            .collect(Collectors.toList());
        
        List<Map<String, Object>> serializedConditions = new ArrayList<>();
        if (apiCommand.getConditions() != null) {
            for (CommandCondition cond : apiCommand.getConditions()) {
                serializedConditions.add(cond.serialize());
            }
        }
        
        return new com.wairesd.discordbm.client.common.models.command.Command.Builder()
            .name(apiCommand.getName())
            .description(apiCommand.getDescription())
            .pluginName(apiCommand.getPluginName())
            .context(apiCommand.getContext())
            .options(options)
            .conditions(serializedConditions)
            .build();
    }

    private CommandOptions convertToInternalOption(CommandOption apiOption) {
        return new CommandOptions.Builder()
            .name(apiOption.getName())
            .description(apiOption.getDescription())
            .type(apiOption.getType())
            .required(apiOption.isRequired())
            .build();
    }

    private CommandOption convertToApiOption(CommandOptions internalOption) {
        return new CommandOptionImpl.Builder()
            .name(internalOption.getName())
            .description(internalOption.getDescription())
            .type(internalOption.getType())
            .required(internalOption.isRequired())
            .build();
    }
} 