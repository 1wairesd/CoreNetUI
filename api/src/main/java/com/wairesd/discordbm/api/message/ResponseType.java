package com.wairesd.discordbm.api.message;

/**
 * Enum for different response types in Discord Bot Manager
 */
public enum ResponseType {
    /**
     * Standard reply to the interaction
     */
    REPLY,
    
    /**
     * Edit an existing message
     */
    EDIT_MESSAGE,
    
    /**
     * Show a modal/form
     */
    MODAL,
    
    /**
     * Reply with a modal/form
     */
    REPLY_MODAL,
    
    /**
     * Send as direct message to user
     */
    DIRECT,
    
    /**
     * Send to specific channel
     */
    CHANNEL,
    
    /**
     * Send random message from list
     */
    RANDOM_REPLY,
    
    /**
     * Reply to specific message
     */
    REPLY_TO_MESSAGE
} 