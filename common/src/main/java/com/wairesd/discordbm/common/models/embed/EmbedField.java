package com.wairesd.discordbm.common.models.embed;

public class EmbedField {
    private final String name;
    private final String value;
    private final boolean inline;

    private EmbedField(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.inline = builder.inline;
    }

    public String name() { return name; }
    public String value() { return value; }
    public boolean inline() { return inline; }

    public static class Builder {
        private String name;
        private String value;
        private boolean inline;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder inline(boolean inline) {
            this.inline = inline;
            return this;
        }

        public EmbedField build() {
            return new EmbedField(this);
        }
    }
}
