package com.wairesd.discordbm.velocity.models.embed;

import java.util.List;

public record EmbedDefinition(
        String title,
        String description,
        Integer color,
        List<EmbedField> fields
) {}