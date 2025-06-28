package com.wairesd.discordbm.common.models.response;

/**
 * Flags for response messages to control behavior
 */
public class ResponseFlags {
    private final boolean preventMessageSend;
    private final boolean isFormResponse;
    private final boolean requiresModal;

    public ResponseFlags(boolean preventMessageSend, boolean isFormResponse, boolean requiresModal) {
        this.preventMessageSend = preventMessageSend;
        this.isFormResponse = isFormResponse;
        this.requiresModal = requiresModal;
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

    public static class Builder {
        private boolean preventMessageSend = false;
        private boolean isFormResponse = false;
        private boolean requiresModal = false;

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

        public ResponseFlags build() {
            return new ResponseFlags(preventMessageSend, isFormResponse, requiresModal);
        }
    }
} 