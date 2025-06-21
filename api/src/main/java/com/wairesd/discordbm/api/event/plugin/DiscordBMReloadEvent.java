package com.wairesd.discordbm.api.event.plugin;

import com.wairesd.discordbm.api.event.Event;

/**
 * Event fired when the plugin is reloaded
 */
public class DiscordBMReloadEvent implements Event {
    
    /**
     * The type of reload
     */
    public enum Type {
        /**
         * Configuration reload
         */
        CONFIG,
        
        /**
         * Network reload
         */
        NETWORK,
        
        /**
         * Command reload
         */
        COMMANDS,
        
        /**
         * Full plugin reload
         */
        FULL
    }
    
    private final Type reloadType;
    
    /**
     * Create a new reload event
     * 
     * @param reloadType The type of reload
     */
    public DiscordBMReloadEvent(Type reloadType) {
        this.reloadType = reloadType;
    }
    
    /**
     * Get the type of reload
     * 
     * @return The type of reload
     */
    public Type getReloadType() {
        return reloadType;
    }
    
    @Override
    public String getType() {
        return "reload";
    }
} 