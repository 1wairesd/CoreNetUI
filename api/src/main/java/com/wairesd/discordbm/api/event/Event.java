package com.wairesd.discordbm.api.event;

/**
 * Base interface for all Discord events
 */
public interface Event {
    
    /**
     * Get the type of the event
     * 
     * @return The event type
     */
    String getType();
} 