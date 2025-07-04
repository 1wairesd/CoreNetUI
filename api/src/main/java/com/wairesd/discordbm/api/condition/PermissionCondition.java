package com.wairesd.discordbm.api.condition;

import com.wairesd.discordbm.api.command.CommandCondition;

import java.util.Map;

public class PermissionCondition implements CommandCondition {
    private final String roleId;

    public PermissionCondition(String roleId) {
        if (roleId == null || roleId.isEmpty()) throw new IllegalArgumentException("Role ID is required");
        this.roleId = roleId;
    }

    @Override
    public String getType() { return "permission"; }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("type", "permission", "role_id", roleId);
    }

    public String getRoleId() { return roleId; }
} 