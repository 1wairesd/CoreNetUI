package com.wairesd.discordbm.common.form;

import com.wairesd.discordbm.api.form.FormField;
import com.wairesd.discordbm.api.form.FormFieldBuilder;

public class FormFieldBuilderImpl implements FormFieldBuilder {
    
    private String label;
    private String placeholder = "";
    private String type = "SHORT";
    private boolean required = false;
    private String variable;

    @Override
    public FormFieldBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public FormFieldBuilder setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public FormFieldBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public FormFieldBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public FormFieldBuilder setVariable(String variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public FormField build() {
        if (label == null || label.isEmpty()) {
            throw new IllegalStateException("Field label is required");
        }
        
        if (variable == null || variable.isEmpty()) {
            throw new IllegalStateException("Field variable is required");
        }
        
        return new FormFieldImpl(label, placeholder, type, required, variable);
    }
} 