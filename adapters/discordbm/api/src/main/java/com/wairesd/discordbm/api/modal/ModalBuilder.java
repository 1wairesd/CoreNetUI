package com.wairesd.discordbm.api.modal;

/**
 * Interface for building Discord modal forms
 */
public interface ModalBuilder {
    
    /**
     * Set the title of the form
     * 
     * @param title The form title
     * @return This builder instance
     */
    ModalBuilder setTitle(String title);
    
    /**
     * Add a field to the form
     * 
     * @param field The field to add
     * @return This builder instance
     */
    ModalBuilder addField(ModalField field);
    
    /**
     * Set the custom ID of the form
     * 
     * @param customId The custom ID
     * @return This builder instance
     */
    ModalBuilder setCustomId(String customId);
    
    /**
     * Build the form
     * 
     * @return The built form
     */
    Modal build();
} 