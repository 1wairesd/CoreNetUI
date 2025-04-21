package com.wairesd.discordbm.velocity.models.embed;

public record EmbedField(
        String name,
        String value,
        boolean inline
) {}