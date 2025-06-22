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
     * Get the required role ID for this command, or null if none is required
     *
     * @return The required role ID, or null if no permission is required
     */
    String getPermission();
    
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
         * Set the required role ID for this command (optional)
         *
         * @param roleId The Discord role ID required to use this command, or null if no permission is required
         * @return This builder
         */
        Builder permission(String roleId);
        
        /**
         * Build the command
         * 
         * @return A new Command instance
         */
        Command build();
    }
} 