package com.wairesd.discordbm.bukkit.models.embed;

public record EmbedField(
        String name,
        String value,
        boolean inline
) {}