package com.wairesd.discordbm.api.command;

/**
 * Represents an option for a Discord slash command
 */
public interface CommandOption {
    
    /**
     * Get the name of the option
     * 
     * @return The option name
     */
    String getName();
    
    /**
     * Get the description of the option
     * 
     * @return The option description
     */
    String getDescription();
    
    /**
     * Get the type of the option
     * 
     * @return The option type
     */
    String getType();
    
    /**
     * Check if this option is required
     * 
     * @return True if required, false otherwise
     */
    boolean isRequired();
    
    /**
     * Builder interface for creating CommandOption instances
     */
    interface Builder {
        /**
         * Set the name of the option
         * 
         * @param name The option name
         * @return This builder
         */
        Builder name(String name);
        
        /**
         * Set the description of the option
         * 
         * @param description The option description
         * @return This builder
         */
        Builder description(String description);
        
        /**
         * Set the type of the option
         * 
         * @param type The option type
         * @return This builder
         */
        Builder type(String type);
        
        /**
         * Set if this option is required
         * 
         * @param required True if required, false otherwise
         * @return This builder
         */
        Builder required(boolean required);
        
        /**
         * Build the command option
         * 
         * @return A new CommandOption instance
         */
        CommandOption build();
    }
} 