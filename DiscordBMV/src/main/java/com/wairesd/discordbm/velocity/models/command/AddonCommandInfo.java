package com.wairesd.discordbm.velocity.models.command;

public record AddonCommandInfo(String commandName, String pluginName) {
    public static AddonCommandInfo create(String commandName, String pluginName) {
        return new AddonCommandInfo(commandName, pluginName);
    }
} 