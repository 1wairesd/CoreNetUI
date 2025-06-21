package com.wairesd.discordbm.common.models.command;

public class CommandOptions {
    private final String name;
    private final String type;
    private final String description;
    private final boolean required;

    private CommandOptions(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.description = builder.description;
        this.required = builder.required;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public static class Builder {
        private String name;
        private String type;
        private String description;
        private boolean required;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public CommandOptions build() {
            return new CommandOptions(this);
        }
    }
}
