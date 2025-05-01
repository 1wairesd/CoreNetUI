package com.wairesd.discordbm.bukkit.api.models.builder;

import com.wairesd.discordbm.bukkit.models.command.Command;
import com.wairesd.discordbm.bukkit.models.command.CommandOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommandBuilder {
    private String name;
    private String description;
    private String pluginName;
    private String context = "both";
    private List<CommandOption> options = new ArrayList<>();

    public CommandBuilder name(String name) {
        this.name = name.toLowerCase().trim();
        return this;
    }

    /**
     * Sets the user-facing command description.
     * @param description Help text for the command
     */
    public CommandBuilder description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the owning plugin name for namespace isolation.
     * @param pluginName Identifier of the registering plugin
     */
    public CommandBuilder pluginName(String pluginName) {
        this.pluginName = pluginName;
        return this;
    }

    /**
     * Sets the command execution context.
     * @param context Where the command can be used (both/dm/server)
     * @throws IllegalArgumentException for invalid context values
     */
    public CommandBuilder context(String context) {
        if (!isValidContext(context)) {
            throw new IllegalArgumentException("Invalid context: " + context
                    + ". Must be 'both', 'dm', or 'server'.");
        }
        this.context = context;
        return this;
    }

    /**
     * Sets the command execution context where this command can be used and
     * returns the updated {@code CommandBuilder} instance.
     *
     * This method ensures that the provided context is valid.
     * The supported contexts are:
     * - "both": Usable in both direct messages (DM) and server channels.
     * - "dm": Usable only in direct messages.
     * - "server": Usable only in server channels.
     *
     * If the context is invalid, an {@code IllegalArgumentException} is thrown.
     *
     * @param context The context in which the command can be executed. Valid values are "both", "dm", or "server".
     * @return The current {@code CommandBuilder} instance with the context updated.
     * @throws IllegalArgumentException If the given context is invalid.
     */
    public CommandBuilder addChoice(String context) {
        if (!isValidContext(context)) {
            throw new IllegalArgumentException("Invalid context: " + context
                    + ". Must be 'both', 'dm', or 'server'.");
        }
        this.context = context;
        return this;
    }

    private boolean isValidContext(String context) {
        return context.equals("both")
                || context.equals("dm")
                || context.equals("server");
    }

    /**
     * Adds an option/parameter to the command.
     * @param name Option identifier (lowercase, no spaces)
     * @param type Option data type
     * @param description User-facing description
     * @param required Whether this option is mandatory
     */
    public CommandBuilder addOption(String name, String type,
                                    String description, boolean required) {
        options.add(new CommandOption(
                name.toLowerCase().trim(),
                type,
                description,
                required
        ));
        return this;
    }

    /**
     * Constructs the final Command object.
     * @throws IllegalStateException if required fields are missing
     */
    public Command build() {
        validate();
        return new Command(name, description, pluginName,
                context, Collections.unmodifiableList(options));
    }

    /**
     * Validates the required fields of the command before building it.
     *
     * This method ensures the following conditions are met:
     * - The command name is not null or empty.
     * - The command description is not null or empty.
     * - The plugin name associated with the command is not null or empty.
     *
     * @throws IllegalStateException if any of the required fields (name, description, or pluginName) are missing or empty.
     */
    private void validate() {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Command name must be set");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalStateException("Command description must be set");
        }
        if (pluginName == null || pluginName.isEmpty()) {
            throw new IllegalStateException("Plugin name must be set");
        }
    }
}