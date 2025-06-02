package com.wairesd.discordbm.common.models.placeholders.response;

public class CanHandleResponse {
    private final String type;
    private final String requestId;
    private final boolean canHandle;

    private CanHandleResponse(Builder builder) {
        this.type = builder.type;
        this.requestId = builder.requestId;
        this.canHandle = builder.canHandle;
    }

    public String type() { return type; }
    public String requestId() { return requestId; }
    public boolean canHandle() { return canHandle; }

    public static class Builder {
        private String type;
        private String requestId;
        private boolean canHandle;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder canHandle(boolean canHandle) {
            this.canHandle = canHandle;
            return this;
        }

        public CanHandleResponse build() {
            return new CanHandleResponse(this);
        }
    }
}
