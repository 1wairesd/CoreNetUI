package com.wairesd.discordbm.common.modal;

import com.wairesd.discordbm.api.modal.ModalField;
import com.wairesd.discordbm.api.modal.ModalFieldBuilder;

public class ModalFieldBuilderImpl implements ModalFieldBuilder {
    
    private String label;
    private String placeholder = "";
    private String type = "SHORT";
    private boolean required = false;
    private String variable;

    @Override
    public ModalFieldBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public ModalFieldBuilder setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public ModalFieldBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public ModalFieldBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public ModalFieldBuilder setVariable(String variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public ModalField build() {
        if (label == null || label.isEmpty()) {
            throw new IllegalStateException("Field label is required");
        }
        
        if (variable == null || variable.isEmpty()) {
            throw new IllegalStateException("Field variable is required");
        }
        
        return new ModalFieldImpl(label, placeholder, type, required, variable);
    }
} 