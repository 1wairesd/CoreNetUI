package com.wairesd.discordbm.common.models.response;

public class RoleActionResponse {
    private final String type = "role_action_response";
    private final String requestId;
    private final boolean success;
    private final String error;

    public RoleActionResponse(String requestId, boolean success, String error) {
        this.requestId = requestId;
        this.success = success;
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public String getRequestId() {
        return requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
} 