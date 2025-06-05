package com.wairesd.discordbm.common.models.embed;

import java.util.List;

public class EmbedDefinition {
    private final String title;
    private final String description;
    private final Integer color;
    private final List<EmbedField> fields;

    private EmbedDefinition(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.color = builder.color;
        this.fields = builder.fields;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }
    public Integer color() {
        return color;
    }

    public List<EmbedField> fields() {
        return fields;
    }

    public static class Builder {
        private String title;
        private String description;
        private Integer color;
        private List<EmbedField> fields;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder color(Integer color) {
            this.color = color;
            return this;
        }

        public Builder fields(List<EmbedField> fields) {
            this.fields = fields;
            return this;
        }

        public EmbedDefinition build() {
            return new EmbedDefinition(this);
        }
    }
}
