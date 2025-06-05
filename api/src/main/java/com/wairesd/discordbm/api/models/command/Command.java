package com.wairesd.discordbm.api.models.command;

import java.util.List;

public class Command {
    private final String name;
    private final String description;
    private final String pluginName;
    private final String context;
    private final List<CommandOption> options;

    private Command(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.pluginName = builder.pluginName;
        this.context = builder.context;
        this.options = builder.options;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getContext() {
        return context;
    }

    public List<CommandOption> getOptions() {
        return options;
    }

    public static class Builder {
        private String name;
        private String description;
        private String pluginName;
        private String context;
        private List<CommandOption> options;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder pluginName(String pluginName) {
            this.pluginName = pluginName;
            return this;
        }

        public Builder context(String context) {
            this.context = context;
            return this;
        }

        public Builder options(List<CommandOption> options) {
            this.options = options;
            return this;
        }

        public Command build() {
            return new Command(this);
        }
    }
}
