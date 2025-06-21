package com.wairesd.discordbm.api.component;

import java.util.Map;

/**
 * Interface for handling Discord component interactions
 */
public interface ComponentHandler {
    
    /**
     * Handle a component interaction
     * 
     * @param componentId The ID of the component
     * @param userData Additional user data from the interaction
     * @param responseCallback Callback to respond to the interaction
     */
    void handleInteraction(String componentId, Map<String, String> userData, InteractionResponseCallback responseCallback);
} 