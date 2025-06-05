package com.wairesd.discordbm.common.models.placeholders.response;

import java.util.Map;

public class PlaceholdersResponse {
    private final String type;
    private final String requestId;
    private final Map<String, String> values;

    private PlaceholdersResponse(Builder builder) {
        this.type = builder.type;
        this.requestId = builder.requestId;
        this.values = builder.values;
    }

    public String type() {
        return type;
    }

    public String requestId() {
        return requestId;
    }

    public Map<String, String> values() {
        return values;
    }

    public static class Builder {
        private String type;
        private String requestId;
        private Map<String, String> values;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder values(Map<String, String> values) {
            this.values = values;
            return this;
        }

        public PlaceholdersResponse build() {
            return new PlaceholdersResponse(this);
        }
    }
}
