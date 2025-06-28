package com.wairesd.discordbm.common.models.response;

import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.form.FormDefinition;

import java.util.List;

public class ResponseMessage {
    private final String type;
    private final String requestId;
    private final String response;
    private final EmbedDefinition embed;
    private final List<ButtonDefinition> buttons;
    private final FormDefinition form;
    private final ResponseFlags flags;

    private ResponseMessage(Builder builder) {
        this.type = builder.type;
        this.requestId = builder.requestId;
        this.response = builder.response;
        this.embed = builder.embed;
        this.buttons = builder.buttons;
        this.form = builder.form;
        this.flags = builder.flags;
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

    public FormDefinition form() {
        return form;
    }

    public ResponseFlags flags() {
        return flags;
    }

    public static class Builder {
        private String type;
        private String requestId;
        private String response;
        private EmbedDefinition embed;
        private List<ButtonDefinition> buttons;
        private FormDefinition form;
        private ResponseFlags flags;

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

        public Builder form(FormDefinition form) {
            this.form = form;
            return this;
        }

        public Builder flags(ResponseFlags flags) {
            this.flags = flags;
            return this;
        }

        public ResponseMessage build() {
            return new ResponseMessage(this);
        }
    }
}
