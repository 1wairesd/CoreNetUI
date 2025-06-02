package com.wairesd.discordbm.common.models.placeholders.request;

import java.util.List;

public class GetPlaceholdersRequest {
    private final String type;
    private final String player;
    private final List<String> placeholders;
    private final String requestId;

    private GetPlaceholdersRequest(Builder builder) {
        this.type = builder.type;
        this.player = builder.player;
        this.placeholders = builder.placeholders;
        this.requestId = builder.requestId;
    }

    public String type() { return type; }
    public String player() { return player; }
    public List<String> placeholders() { return placeholders; }
    public String requestId() { return requestId; }

    public static class Builder {
        private String type;
        private String player;
        private List<String> placeholders;
        private String requestId;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder player(String player) {
            this.player = player;
            return this;
        }

        public Builder placeholders(List<String> placeholders) {
            this.placeholders = placeholders;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public GetPlaceholdersRequest build() {
            return new GetPlaceholdersRequest(this);
        }
    }
}
