package com.wairesd.discordbm.velocity.api;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

/**
 * Wrapper class for Velocity CommandSource to provide simplified message sending
 */
public class VelocityCommandSender {
    private final CommandSource source;

    /**
     * Creates a new VelocityCommandSender with the specified CommandSource
     *
     * @param source The CommandSource to wrap
     */
    public VelocityCommandSender(CommandSource source) {
        this.source = source;
    }

    /**
     * Sends a text message to the command source
     *
     * @param message The message to send
     */
    public void sendMessage(String message) {
        source.sendMessage(Component.text(message));
    }

    /**
     * Sends a component message to the command source
     *
     * @param component The component to send
     */
    public void sendMessage(Component component) {
        source.sendMessage(component);
    }

    /**
     * Gets the name of the command source
     *
     * @return The name as a string
     */
    public String getName() {
        return source.toString();
    }

    /**
     * Gets the underlying CommandSource
     *
     * @return The CommandSource instance
     */
    public CommandSource getSource() {
        return source;
    }
} 