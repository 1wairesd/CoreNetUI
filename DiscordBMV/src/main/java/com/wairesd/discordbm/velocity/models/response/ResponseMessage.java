package com.wairesd.discordbm.velocity.models.response;

import com.wairesd.discordbm.velocity.models.embed.EmbedDefinition;

public record ResponseMessage(
        String type,
        String requestId,
        String response,
        EmbedDefinition embed
) {}