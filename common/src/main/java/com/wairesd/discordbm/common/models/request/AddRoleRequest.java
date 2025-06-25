package com.wairesd.discordbm.common.models.request;

public class AddRoleRequest {
    private final String type = "add_role";
    private final String guildId;
    private final String userId;
    private final String roleId;
    private final String requestId;

    public AddRoleRequest(String guildId, String userId, String roleId, String requestId) {
        this.guildId = guildId;
        this.userId = userId;
        this.roleId = roleId;
        this.requestId = requestId;
    }

    public String getType() {
        return type;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRequestId() {
        return requestId;
    }
} 