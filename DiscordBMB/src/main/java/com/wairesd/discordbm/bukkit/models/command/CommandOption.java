package com.wairesd.discordbm.bukkit.models.command;

// Represents an option for a command.
public class CommandOption {
    public String name;
    public String type;
    public String description;
    public boolean required;

    public CommandOption(String name, String type, String description, boolean required) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.required = required;
    }
}