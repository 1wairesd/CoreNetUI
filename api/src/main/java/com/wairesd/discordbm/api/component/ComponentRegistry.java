package com.wairesd.discordbm.api.component;

/**
 * Interface for registering and managing Discord components
 */
public interface ComponentRegistry {
    
    /**
     * Register a button click handler
     * 
     * @param customId The custom ID of the button
     * @param handler The handler to call when the button is clicked
     */
    void registerButtonHandler(String customId, ComponentHandler handler);
    
    /**
     * Unregister a button click handler
     * 
     * @param customId The custom ID of the button
     */
    void unregisterButtonHandler(String customId);
    
    /**
     * Create a new button builder
     * 
     * @return A new button builder
     */
    Button.Builder createButtonBuilder();
} 