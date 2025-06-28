package com.wairesd.discordbm.client.common.form;

import com.wairesd.discordbm.api.form.Form;
import com.wairesd.discordbm.api.form.FormField;
import com.wairesd.discordbm.common.models.form.FormDefinition;
import com.wairesd.discordbm.common.models.form.FormFieldDefinition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter for converting API Form to internal FormDefinition
 */
public class FormAdapter {
    
    private final Form form;
    
    public FormAdapter(Form form) {
        this.form = form;
    }
    
    public FormDefinition getInternalForm() {
        List<FormFieldDefinition> fieldDefs = form.getFields().stream()
                .map(this::convertToFieldDefinition)
                .collect(Collectors.toList());
        
        return new FormDefinition.Builder()
                .title(form.getTitle())
                .customId(form.getCustomId())
                .fields(fieldDefs)
                .build();
    }
    
    private FormFieldDefinition convertToFieldDefinition(FormField field) {
        return new FormFieldDefinition.Builder()
                .label(field.getLabel())
                .placeholder(field.getPlaceholder())
                .type(field.getType())
                .required(field.isRequired())
                .variable(field.getVariable())
                .build();
    }
} 