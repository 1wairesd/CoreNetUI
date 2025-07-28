package com.wairesd.discordbm.api.modal;

/**
 * Interface for building form fields
 */
public interface ModalFieldBuilder {
    
    /**
     * Set the label of the field
     * 
     * @param label The field label
     * @return This builder instance
     */
    ModalFieldBuilder setLabel(String label);
    
    /**
     * Set the placeholder text for the field
     * 
     * @param placeholder The placeholder text
     * @return This builder instance
     */
    ModalFieldBuilder setPlaceholder(String placeholder);
    
    /**
     * Set the type of the field
     * 
     * @param type The field type (SHORT, PARAGRAPH)
     * @return This builder instance
     */
    ModalFieldBuilder setType(String type);
    
    /**
     * Set if the field is required
     * 
     * @param required True if required, false otherwise
     * @return This builder instance
     */
    ModalFieldBuilder setRequired(boolean required);
    
    /**
     * Set the variable name for the field
     * 
     * @param variable The variable name
     * @return This builder instance
     */
    ModalFieldBuilder setVariable(String variable);
    
    /**
     * Build the form field
     * 
     * @return The built form field
     */
    ModalField build();
} 