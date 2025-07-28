package com.wairesd.discordbm.common.models.modal;

import java.util.List;

/**
 * Model for form definition used in network communication
 */
public class ModalDefinition {
    private final String title;
    private final String customId;
    private final List<ModalFieldDefinition> fields;

    public ModalDefinition(String title, String customId, List<ModalFieldDefinition> fields) {
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

    public List<ModalFieldDefinition> getFields() {
        return fields;
    }

    public static class Builder {
        private String title;
        private String customId;
        private List<ModalFieldDefinition> fields;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder customId(String customId) {
            this.customId = customId;
            return this;
        }

        public Builder fields(List<ModalFieldDefinition> fields) {
            this.fields = fields;
            return this;
        }

        public ModalDefinition build() {
            return new ModalDefinition(title, customId, fields);
        }
    }
} 