package com.wairesd.discordbm.common.models.response;

import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;

import java.util.List;

public class ResponseMessage {
    private final String type;
    private final String requestId;
    private final String response;
    private final EmbedDefinition embed;
    private final List<ButtonDefinition> buttons;

    private ResponseMessage(Builder builder) {
        this.type = builder.type;
        this.requestId = builder.requestId;
        this.response = builder.response;
        this.embed = builder.embed;
        this.buttons = builder.buttons;
    }

    public String type() {
        return type;
    }

    public String requestId() {
        return requestId;
    }

    public String response() {
        return response;
    }

    public EmbedDefinition embed() {
        return embed;
    }

    public List<ButtonDefinition> buttons() {
        return buttons;
    }

    public static class Builder {
        private String type;
        private String requestId;
        private String response;
        private EmbedDefinition embed;
        private List<ButtonDefinition> buttons;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder response(String response) {
            this.response = response;
            return this;
        }

        public Builder embed(EmbedDefinition embed) {
            this.embed = embed;
            return this;
        }

        public Builder buttons(List<ButtonDefinition> buttons) {
            this.buttons = buttons;
            return this;
        }

        public ResponseMessage build() {
            return new ResponseMessage(this);
        }
    }
}
