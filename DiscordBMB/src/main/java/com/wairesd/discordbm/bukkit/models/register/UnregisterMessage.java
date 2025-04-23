package com.wairesd.discordbm.bukkit.models.register;

public class UnregisterMessage {
    public String type = "unregister";
    public String serverName;
    public String pluginName;
    public String commandName;
    public String secret;

    public UnregisterMessage(String serverName, String pluginName, String commandName, String secret) {
        this.serverName = serverName;
        this.pluginName = pluginName;
        this.commandName = commandName;
        this.secret = secret;
    }
}