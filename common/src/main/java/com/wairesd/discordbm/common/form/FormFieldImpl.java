package com.wairesd.discordbm.common.form;

import com.wairesd.discordbm.api.form.FormField;

public class FormFieldImpl implements FormField {
    
    private final String label;
    private final String placeholder;
    private final String type;
    private final boolean required;
    private final String variable;

    public FormFieldImpl(String label, String placeholder, String type, boolean required, String variable) {
        this.label = label;
        this.placeholder = placeholder;
        this.type = type;
        this.required = required;
        this.variable = variable;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String getVariable() {
        return variable;
    }
} 