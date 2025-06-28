package com.wairesd.discordbm.common.models.form;

/**
 * Model for form field definition used in network communication
 */
public class FormFieldDefinition {
    private final String label;
    private final String placeholder;
    private final String type;
    private final boolean required;
    private final String variable;

    public FormFieldDefinition(String label, String placeholder, String type, boolean required, String variable) {
        this.label = label;
        this.placeholder = placeholder;
        this.type = type;
        this.required = required;
        this.variable = variable;
    }

    public String getLabel() {
        return label;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public String getVariable() {
        return variable;
    }

    public static class Builder {
        private String label;
        private String placeholder;
        private String type;
        private boolean required;
        private String variable;

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder variable(String variable) {
            this.variable = variable;
            return this;
        }

        public FormFieldDefinition build() {
            return new FormFieldDefinition(label, placeholder, type, required, variable);
        }
    }
} 