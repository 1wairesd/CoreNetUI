package com.wairesd.discordbm.velocity.command.custom.models;

import java.util.List;

public class CustomCommand {
    private final String name;
    private final String description;
    private final String context;
    private final List<CommandOption> options;
    private final List<CommandCondition> conditions;
    private final List<CommandAction> actions;

    public CustomCommand(String name, String description, String context,
                         List<CommandOption> options, List<CommandCondition> conditions,
                         List<CommandAction> actions) {
        validateInputs(name, description, context);
        this.name = name;
        this.description = description;
        this.context = context;
        this.options = options != null ? List.copyOf(options) : List.of();
        this.conditions = conditions != null ? List.copyOf(conditions) : List.of();
        this.actions = actions != null ? List.copyOf(actions) : List.of();
    }

    private void validateInputs(String name, String description, String context) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Command name is required");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Command description is required");
        }
        if (!List.of("both", "dm", "server").contains(context)) {
            throw new IllegalArgumentException("Invalid context: " + context);
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getContext() { return context; }
    public List<CommandOption> getOptions() { return options; }
    public List<CommandCondition> getConditions() { return conditions; }
    public List<CommandAction> getActions() { return actions; }
}