package com.wairesd.discordbm.client.common.models.command;

import java.util.List;
import java.util.Map;

public class Command {
    private final String name;
    private final String description;
    private final String pluginName;
    private final String context;
    private final List<CommandOptions> options;
    private final String permission;
    private final List<Map<String, Object>> conditions;

    private Command(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.pluginName = builder.pluginName;
        this.context = builder.context;
        this.options = builder.options;
        this.permission = builder.permission;
        this.conditions = builder.conditions;
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

    public List<CommandOptions> getOptions() {
        return options;
    }

    public String getPermission() {
        return permission;
    }

    public List<Map<String, Object>> getConditions() {
        return conditions;
    }

    public static class Builder {
        private String name;
        private String description;
        private String pluginName;
        private String context;
        private List<CommandOptions> options;
        private String permission;
        private List<Map<String, Object>> conditions;

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

        public Builder options(List<CommandOptions> options) {
            this.options = options;
            return this;
        }

        public Builder permission(String roleId) {
            this.permission = roleId;
            return this;
        }

        public Builder conditions(List<Map<String, Object>> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Command build() {
            return new Command(this);
        }
    }
}
