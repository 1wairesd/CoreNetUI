package com.wairesd.discordbm.api.event;

/**
 * Interface for registering and managing event listeners
 */
public interface EventRegistry {
    
    /**
     * Register an event listener
     * 
     * @param <T> The type of event
     * @param listener The listener to register
     */
    <T extends Event> void registerListener(EventListener<T> listener);
    
    /**
     * Unregister an event listener
     * 
     * @param <T> The type of event
     * @param listener The listener to unregister
     */
    <T extends Event> void unregisterListener(EventListener<T> listener);
    
    /**
     * Fire an event to all registered listeners
     * 
     * @param <T> The type of event
     * @param event The event to fire
     */
    <T extends Event> void fireEvent(T event);
} 