package com.wairesd.discordbm.api.form;

import java.util.List;

/**
 * Interface for Discord modal forms
 */
public interface Form {
    
    /**
     * Get the title of the form
     * 
     * @return The form title
     */
    String getTitle();
    
    /**
     * Get the fields of the form
     * 
     * @return List of form fields
     */
    List<FormField> getFields();
    
    /**
     * Get the custom ID of the form
     * 
     * @return The form custom ID
     */
    String getCustomId();
} 