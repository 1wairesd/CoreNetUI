package com.wairesd.discordbm.bukkit.models.register;

import com.wairesd.discordbm.bukkit.models.embed.EmbedDefinition;

/**
 * Represents a response message sent from the Bukkit server back to the Discord bot.
 * Contains the original request ID and the response content.
 */
public class ResponseMessage {
    public String type = "response";
    public String requestId;
    public String response;
    public EmbedDefinition embed;

    public ResponseMessage(String type, String requestId, String response, EmbedDefinition embed) {
        this.type = type;
        this.requestId = requestId;
        this.response = response;
        this.embed = embed;
    }
}