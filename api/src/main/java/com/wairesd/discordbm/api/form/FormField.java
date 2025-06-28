package com.wairesd.discordbm.api.form;

/**
 * Interface for form fields in Discord modal forms
 */
public interface FormField {
    
    /**
     * Get the label of the field
     * 
     * @return The field label
     */
    String getLabel();
    
    /**
     * Get the placeholder text for the field
     * 
     * @return The placeholder text
     */
    String getPlaceholder();
    
    /**
     * Get the type of the field (SHORT, PARAGRAPH)
     * 
     * @return The field type
     */
    String getType();
    
    /**
     * Check if the field is required
     * 
     * @return True if required, false otherwise
     */
    boolean isRequired();
    
    /**
     * Get the variable name for the field
     * 
     * @return The variable name
     */
    String getVariable();
} 