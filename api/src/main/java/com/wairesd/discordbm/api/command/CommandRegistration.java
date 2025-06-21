package com.wairesd.discordbm.api.command;

import java.util.List;

/**
 * Interface for registering and unregistering Discord commands
 */
public interface CommandRegistration {
    
    /**
     * Register a command with a handler
     * 
     * @param command The command to register
     * @param handler The handler for the command
     */
    void registerCommand(Command command, CommandHandler handler);
    
    /**
     * Register a command with a handler and a listener
     * 
     * @param command The command to register
     * @param handler The handler for the command
     * @param listener The listener for command events
     */
    void registerCommand(Command command, CommandHandler handler, CommandListener listener);
    
    /**
     * Unregister a command
     * 
     * @param commandName The name of the command to unregister
     * @param pluginName The name of the plugin that registered the command
     */
    void unregisterCommand(String commandName, String pluginName);
    
    /**
     * Get all registered commands
     * 
     * @return A list of all registered commands
     */
    List<Command> getRegisteredCommands();
    
    /**
     * Create a new command builder
     * 
     * @return A new command builder
     */
    Command.Builder createCommandBuilder();
    
    /**
     * Create a new command option builder
     * 
     * @return A new command option builder
     */
    CommandOption.Builder createOptionBuilder();
} 