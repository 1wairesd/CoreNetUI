package com.wairesd.discordbm.common.models.response;

/**
 * Flags for response messages to control behavior
 */
public class ResponseFlags {
    private final boolean preventMessageSend;
    private final boolean isFormResponse;
    private final boolean requiresModal;
    private final boolean ephemeral;
    private final String responseType;

    public ResponseFlags(boolean preventMessageSend, boolean isFormResponse, boolean requiresModal) {
        this.preventMessageSend = preventMessageSend;
        this.isFormResponse = isFormResponse;
        this.requiresModal = requiresModal;
        this.ephemeral = false;
        this.responseType = "default";
    }

    public ResponseFlags(Builder builder) {
        this.preventMessageSend = builder.preventMessageSend;
        this.isFormResponse = builder.isFormResponse;
        this.requiresModal = builder.requiresModal;
        this.ephemeral = builder.ephemeral;
        this.responseType = builder.responseType;
    }

    public boolean shouldPreventMessageSend() {
        return preventMessageSend;
    }

    public boolean isFormResponse() {
        return isFormResponse;
    }

    public boolean requiresModal() {
        return requiresModal;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }
    public String getResponseType() {
        return responseType;
    }

    public static class Builder {
        private boolean preventMessageSend = false;
        private boolean isFormResponse = false;
        private boolean requiresModal = false;
        private boolean ephemeral;
        private String responseType;

        public Builder preventMessageSend(boolean prevent) {
            this.preventMessageSend = prevent;
            return this;
        }

        public Builder isFormResponse(boolean isForm) {
            this.isFormResponse = isForm;
            return this;
        }

        public Builder requiresModal(boolean requiresModal) {
            this.requiresModal = requiresModal;
            return this;
        }

        public Builder ephemeral(boolean ephemeral) {
            this.ephemeral = ephemeral;
            return this;
        }
        public Builder responseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public ResponseFlags build() {
            return new ResponseFlags(this);
        }
    }
} 