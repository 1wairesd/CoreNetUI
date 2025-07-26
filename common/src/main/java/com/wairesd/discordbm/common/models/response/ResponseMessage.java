package com.wairesd.discordbm.common.models.response;

import com.google.gson.annotations.SerializedName;
import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.form.FormDefinition;

import java.util.List;
import java.util.Map;

public class ResponseMessage {
    private final String type;
    private final String requestId;
    private final String response;
    private final EmbedDefinition embed;
    private final List<ButtonDefinition> buttons;
    private final FormDefinition form;
    private final ResponseFlags flags;
    private final String userId;
    private final String channelId;
    private final List<Map<String, Object>> conditions;
    private final boolean deleteAll;
    private final List<String> responses;
    @SerializedName("replyMessageId")
    private String replyMessageId;
    @SerializedName("replyMentionAuthor")
    private Boolean replyMentionAuthor;
    private final String errorType;
    private final Map<String, String> errorPlaceholders;

    private ResponseMessage(Builder builder) {
        this.type = builder.type;
        this.requestId = builder.requestId;
        this.response = builder.response;
        this.embed = builder.embed;
        this.buttons = builder.buttons;
        this.form = builder.form;
        this.flags = builder.flags;
        this.userId = builder.userId;
        this.channelId = builder.channelId;
        this.conditions = builder.conditions;
        this.deleteAll = builder.deleteAll;
        this.responses = builder.responses;
        this.replyMessageId = builder.replyMessageId;
        this.replyMentionAuthor = builder.replyMentionAuthor;
        this.errorType = builder.errorType;
        this.errorPlaceholders = builder.errorPlaceholders;
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

    public String userId() {
        return userId;
    }

    public String channelId() {
        return channelId;
    }

    public List<Map<String, Object>> conditions() {
        return conditions;
    }

    public boolean deleteAll() {
        return deleteAll;
    }

    public List<String> responses() {
        return responses;
    }

    public String replyMessageId() {
        return replyMessageId;
    }
    public Boolean replyMentionAuthor() {
        return replyMentionAuthor;
    }
    
    public String errorType() {
        return errorType;
    }
    
    public Map<String, String> errorPlaceholders() {
        return errorPlaceholders;
    }

    public static class Builder {
        private String type;
        private String requestId;
        private String response;
        private EmbedDefinition embed;
        private List<ButtonDefinition> buttons;
        private FormDefinition form;
        private ResponseFlags flags;
        private String userId;
        private String channelId;
        private List<Map<String, Object>> conditions;
        private boolean deleteAll = true;
        private List<String> responses;
        private String replyMessageId;
        private Boolean replyMentionAuthor;
        private String errorType;
        private Map<String, String> errorPlaceholders;

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

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder conditions(List<Map<String, Object>> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder deleteAll(boolean deleteAll) {
            this.deleteAll = deleteAll;
            return this;
        }

        public Builder responses(List<String> responses) {
            this.responses = responses;
            return this;
        }

        public Builder replyMessageId(String replyMessageId) {
            this.replyMessageId = replyMessageId;
            return this;
        }
        public Builder replyMentionAuthor(Boolean replyMentionAuthor) {
            this.replyMentionAuthor = replyMentionAuthor;
            return this;
        }
        
        public Builder errorType(String errorType) {
            this.errorType = errorType;
            return this;
        }
        
        public Builder errorPlaceholders(Map<String, String> errorPlaceholders) {
            this.errorPlaceholders = errorPlaceholders;
            return this;
        }

        public ResponseMessage build() {
            return new ResponseMessage(this);
        }
    }
}
