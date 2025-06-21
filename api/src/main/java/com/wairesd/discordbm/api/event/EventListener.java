package com.wairesd.discordbm.api.event;

/**
 * Interface for listening to Discord events
 * 
 * @param <T> The type of event to listen for
 */
public interface EventListener<T extends Event> {
    
    /**
     * Called when an event occurs
     * 
     * @param event The event that occurred
     */
    void onEvent(T event);
    
    /**
     * Get the type of event this listener is interested in
     * 
     * @return The event class
     */
    Class<T> getEventType();
} 