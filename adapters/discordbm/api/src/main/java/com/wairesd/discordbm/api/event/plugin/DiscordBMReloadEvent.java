package com.wairesd.discordbm.api.event.plugin;

import com.wairesd.discordbm.api.event.DBMEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Data
public class DiscordBMReloadEvent extends DBMEvent {

    private final Type type;
    
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
} 