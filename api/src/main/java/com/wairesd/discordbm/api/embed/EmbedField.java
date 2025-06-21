package com.wairesd.discordbm.api.embed;

/**
 * Interface representing a field in a Discord embed
 */
public interface EmbedField {
    
    /**
     * Get the name of the field
     * 
     * @return The field name
     */
    String getName();
    
    /**
     * Get the value of the field
     * 
     * @return The field value
     */
    String getValue();
    
    /**
     * Check if the field is inline
     * 
     * @return True if inline, false otherwise
     */
    boolean isInline();
    
    /**
     * Builder interface for creating EmbedField instances
     */
    interface Builder {
        /**
         * Set the name of the field
         * 
         * @param name The field name
         * @return This builder
         */
        Builder name(String name);
        
        /**
         * Set the value of the field
         * 
         * @param value The field value
         * @return This builder
         */
        Builder value(String value);
        
        /**
         * Set if the field is inline
         * 
         * @param inline True if inline, false otherwise
         * @return This builder
         */
        Builder inline(boolean inline);
        
        /**
         * Build the field
         * 
         * @return A new EmbedField instance
         */
        EmbedField build();
    }
} 