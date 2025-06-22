package com.wairesd.discordbm.client.common.command;

import com.wairesd.discordbm.api.command.Command;
import com.wairesd.discordbm.api.command.CommandOption;
import com.wairesd.discordbm.client.common.models.command.CommandOptions;

import java.util.List;
import java.util.stream.Collectors;

public class CommandAdapter implements Command {
    
    private final Command apiCommand;
    private com.wairesd.discordbm.client.common.models.command.Command internalCommand;

    public CommandAdapter(Command apiCommand) {
        this.apiCommand = apiCommand;
        this.internalCommand = convertToInternalCommand(apiCommand);
    }

    public CommandAdapter(com.wairesd.discordbm.client.common.models.command.Command internalCommand) {
        this.internalCommand = internalCommand;
        this.apiCommand = convertToApiCommand(internalCommand);
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
    public String getPermission() {
        return apiCommand.getPermission();
    }

    public com.wairesd.discordbm.client.common.models.command.Command getInternalCommand() {
        return internalCommand;
    }

    private com.wairesd.discordbm.client.common.models.command.Command convertToInternalCommand(Command apiCommand) {
        List<CommandOptions> options = apiCommand.getOptions().stream()
            .map(this::convertToInternalOption)
            .collect(Collectors.toList());
        
        return new com.wairesd.discordbm.client.common.models.command.Command.Builder()
            .name(apiCommand.getName())
            .description(apiCommand.getDescription())
            .pluginName(apiCommand.getPluginName())
            .context(apiCommand.getContext())
            .options(options)
            .permission(apiCommand.getPermission())
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

    private Command convertToApiCommand(com.wairesd.discordbm.client.common.models.command.Command internalCommand) {
        List<CommandOption> options = internalCommand.getOptions().stream()
            .map(this::convertToApiOption)
            .collect(Collectors.toList());
        
        return new CommandImpl.Builder()
            .name(internalCommand.getName())
            .description(internalCommand.getDescription())
            .pluginName(internalCommand.getPluginName())
            .context(internalCommand.getContext())
            .options(options)
            .permission(internalCommand.getPermission())
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