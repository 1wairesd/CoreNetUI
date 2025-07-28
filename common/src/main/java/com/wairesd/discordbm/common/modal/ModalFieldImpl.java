package com.wairesd.discordbm.common.modal;

import com.wairesd.discordbm.api.modal.ModalField;

public class ModalFieldImpl implements ModalField {
    
    private final String label;
    private final String placeholder;
    private final String type;
    private final boolean required;
    private final String variable;

    public ModalFieldImpl(String label, String placeholder, String type, boolean required, String variable) {
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