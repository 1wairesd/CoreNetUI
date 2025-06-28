package com.wairesd.discordbm.common.models.command;

import java.util.List;

public record CommandDefinition(
        String name,
        String description,
        String context,
        List<CommandOption> options,
        String permission,
        List<String> conditions,
        boolean canSendForms
) {
    public record CommandOption(
            String name,
            String description,
            String type,
            boolean required
    ) {}
} 