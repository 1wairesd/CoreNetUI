package com.wairesd.discordbm.velocity.commands.commandbuilder.strategy;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.ResponseType;
import com.wairesd.discordbm.velocity.commands.commandbuilder.responses.DirectMessageResponse;
import com.wairesd.discordbm.velocity.commands.commandbuilder.responses.EditMessageResponse;
import com.wairesd.discordbm.velocity.commands.commandbuilder.responses.ReplyResponse;
import com.wairesd.discordbm.velocity.commands.commandbuilder.responses.SpecificChannelResponse;

public class ResponseStrategyFactory {
    public static ResponseStrategy getStrategy(ResponseType type) {
        return switch (type) {
            case SPECIFIC_CHANNEL -> new SpecificChannelResponse();
            case DIRECT_MESSAGE -> new DirectMessageResponse();
            case EDIT_MESSAGE -> new EditMessageResponse();
            case REPLY -> new ReplyResponse();
        };
    }
}
