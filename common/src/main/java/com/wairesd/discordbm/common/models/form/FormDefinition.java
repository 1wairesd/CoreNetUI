package com.wairesd.discordbm.common.models.form;

import java.util.List;

/**
 * Model for form definition used in network communication
 */
public class FormDefinition {
    private final String title;
    private final String customId;
    private final List<FormFieldDefinition> fields;

    public FormDefinition(String title, String customId, List<FormFieldDefinition> fields) {
        this.title = title;
        this.customId = customId;
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public String getCustomId() {
        return customId;
    }

    public List<FormFieldDefinition> getFields() {
        return fields;
    }

    public static class Builder {
        private String title;
        private String customId;
        private List<FormFieldDefinition> fields;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder customId(String customId) {
            this.customId = customId;
            return this;
        }

        public Builder fields(List<FormFieldDefinition> fields) {
            this.fields = fields;
            return this;
        }

        public FormDefinition build() {
            return new FormDefinition(title, customId, fields);
        }
    }
} 