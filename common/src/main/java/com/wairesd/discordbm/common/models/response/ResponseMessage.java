package com.wairesd.discordbm.common.models.response;

import com.wairesd.discordbm.common.models.embed.EmbedDefinition;

public record ResponseMessage(
        String type,
        String requestId,
        String response,
        EmbedDefinition embed
) {}
