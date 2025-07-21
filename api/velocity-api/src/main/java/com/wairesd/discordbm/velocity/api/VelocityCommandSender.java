package com.wairesd.discordbm.velocity.api;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

public class VelocityCommandSender {
    private final CommandSource source;

    public VelocityCommandSender(CommandSource source) {
        this.source = source;
    }

    public void sendMessage(String message) {
        source.sendMessage(Component.text(message));
    }

    public void sendMessage(Component component) {
        source.sendMessage(component);
    }

    public String getName() {
        return source.toString();
    }

    public CommandSource getSource() {
        return source;
    }
} 