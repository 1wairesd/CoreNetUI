package com.wairesd.discordbm.client.common.command;

import com.wairesd.discordbm.api.command.CommandOption;

public class CommandOptionImpl implements CommandOption {
    
    private final String name;
    private final String description;
    private final String type;
    private final boolean required;

    private CommandOptionImpl(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.required = builder.required;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public boolean isRequired() {
        return required;
    }

    public static class Builder implements CommandOption.Builder {
        private String name;
        private String description;
        private String type;
        private boolean required;
        
        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        @Override
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        @Override
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }
        
        @Override
        public CommandOption build() {
            return new CommandOptionImpl(this);
        }
    }
} 