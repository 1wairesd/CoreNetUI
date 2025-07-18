package com.wairesd.discordbm.host.common.commandbuilder.interaction.strategy;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.ResponseType;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.DirectMessageResponse;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.EditMessageResponse;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.ReplyResponse;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.SpecificChannelResponse;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.RandomReplyResponse;

public class ResponseStrategyFactory {
    public static ResponseStrategy getStrategy(ResponseType type) {
        return switch (type) {
            case SPECIFIC_CHANNEL -> new SpecificChannelResponse();
            case DIRECT_MESSAGE -> new DirectMessageResponse();
            case EDIT_MESSAGE -> new EditMessageResponse();
            case REPLY -> new ReplyResponse();
            case RANDOM_REPLY -> new RandomReplyResponse();
        };
    }
}
