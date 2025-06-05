package com.wairesd.discordbm.common.models.register;

import java.util.List;

public class RegisterMessage<T> {
    private final String type;
    private final String serverName;
    private final String pluginName;
    private final List<T> commands;
    private final String secret;

    private RegisterMessage(Builder<T> builder) {
        this.type = builder.type;
        this.serverName = builder.serverName;
        this.pluginName = builder.pluginName;
        this.commands = builder.commands;
        this.secret = builder.secret;
    }

    public String type() {
        return type;
    }

    public String serverName() {
        return serverName;
    }

    public String pluginName() {
        return pluginName;
    }

    public List<T> commands() {
        return commands;
    }

    public String secret() {
        return secret;
    }

    public static class Builder<T> {
        private String type;
        private String serverName;
        private String pluginName;
        private List<T> commands;
        private String secret;

        public Builder<T> type(String type) {
            this.type = type;
            return this;
        }

        public Builder<T> serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public Builder<T> pluginName(String pluginName) {
            this.pluginName = pluginName;
            return this;
        }

        public Builder<T> commands(List<T> commands) {
            this.commands = commands;
            return this;
        }

        public Builder<T> secret(String secret) {
            this.secret = secret;
            return this;
        }

        public RegisterMessage<T> build() {
            return new RegisterMessage<>(this);
        }
    }
}
