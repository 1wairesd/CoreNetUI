package com.wairesd.discordbm.api.component;

/**
 * Interface representing a Discord button
 */
public interface Button {
    
    /**
     * Get the label of the button
     * 
     * @return The button label
     */
    String getLabel();
    
    /**
     * Get the custom ID of the button
     * 
     * @return The custom ID
     */
    String getCustomId();
    
    /**
     * Get the style of the button
     * 
     * @return The button style
     */
    ButtonStyle getStyle();
    
    /**
     * Get the URL of the button (only for link buttons)
     * 
     * @return The URL or null if not a link button
     */
    String getUrl();
    
    /**
     * Check if the button is disabled
     * 
     * @return True if disabled, false otherwise
     */
    boolean isDisabled();
    
    /**
     * Builder interface for creating Button instances
     */
    interface Builder {
        /**
         * Set the label of the button
         * 
         * @param label The button label
         * @return This builder
         */
        Builder label(String label);
        
        /**
         * Set the custom ID of the button
         * 
         * @param customId The custom ID
         * @return This builder
         */
        Builder customId(String customId);
        
        /**
         * Set the style of the button
         * 
         * @param style The button style
         * @return This builder
         */
        Builder style(ButtonStyle style);
        
        /**
         * Set the URL of the button (only for link buttons)
         * 
         * @param url The URL
         * @return This builder
         */
        Builder url(String url);
        
        /**
         * Set if the button is disabled
         * 
         * @param disabled True if disabled, false otherwise
         * @return This builder
         */
        Builder disabled(boolean disabled);
        
        /**
         * Build the button
         * 
         * @return A new Button instance
         */
        Button build();
    }
} 