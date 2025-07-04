package com.wairesd.discordbm.api.command;

import java.util.List;

/**
 * Represents a Discord slash command
 */
public interface Command {
    
    /**
     * Get the name of the command
     * 
     * @return The command name
     */
    String getName();
    
    /**
     * Get the description of the command
     * 
     * @return The command description
     */
    String getDescription();
    
    /**
     * Get the plugin name that registered this command
     * 
     * @return The plugin name
     */
    String getPluginName();
    
    /**
     * Get the context in which this command can be used
     * 
     * @return The command context
     */
    String getContext();
    
    /**
     * Get the options for this command
     * 
     * @return The command options
     */
    List<CommandOption> getOptions();
    
    /**
     * Get the conditions for this command
     *
     * @return The command conditions
     */
    List<CommandCondition> getConditions();
    
    /**
     * Builder interface for creating Command instances
     */
    interface Builder {
        /**
         * Set the name of the command
         * 
         * @param name The command name
         * @return This builder
         */
        Builder name(String name);
        
        /**
         * Set the description of the command
         * 
         * @param description The command description
         * @return This builder
         */
        Builder description(String description);
        
        /**
         * Set the plugin name that is registering this command
         * 
         * @param pluginName The plugin name
         * @return This builder
         */
        Builder pluginName(String pluginName);
        
        /**
         * Set the context in which this command can be used
         * 
         * @param context The command context
         * @return This builder
         */
        Builder context(String context);
        
        /**
         * Set the options for this command
         * 
         * @param options The command options
         * @return This builder
         */
        Builder options(List<CommandOption> options);
        
        /**
         * Add a condition to the command
         *
         * @param condition The command condition
         * @return This builder
         */
        Builder addCondition(CommandCondition condition);
        
        /**
         * Set all conditions for the command
         *
         * @param conditions The list of conditions
         * @return This builder
         */
        Builder conditions(List<CommandCondition> conditions);
        
        /**
         * Build the command
         * 
         * @return A new Command instance
         */
        Command build();
    }
} 