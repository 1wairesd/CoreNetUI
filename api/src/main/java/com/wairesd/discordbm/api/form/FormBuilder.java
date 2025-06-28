package com.wairesd.discordbm.api.form;

/**
 * Interface for building Discord modal forms
 */
public interface FormBuilder {
    
    /**
     * Set the title of the form
     * 
     * @param title The form title
     * @return This builder instance
     */
    FormBuilder setTitle(String title);
    
    /**
     * Add a field to the form
     * 
     * @param field The field to add
     * @return This builder instance
     */
    FormBuilder addField(FormField field);
    
    /**
     * Set the custom ID of the form
     * 
     * @param customId The custom ID
     * @return This builder instance
     */
    FormBuilder setCustomId(String customId);
    
    /**
     * Build the form
     * 
     * @return The built form
     */
    Form build();
} 