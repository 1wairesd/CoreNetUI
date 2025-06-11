package com.wairesd.discordbm.velocity.commandbuilder.security.conditions.permissions;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.conditions.CommandCondition;
import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;

import java.util.Map;

public class NoRoleCondition implements CommandCondition {
    private final String roleId;

    public NoRoleCondition(Map<String, Object> properties) {
        this.roleId = (String) properties.get("role_id");
        if (this.roleId == null || this.roleId.isEmpty()) {
            throw new IllegalArgumentException("role_id is required for NotHaveRoleCondition");
        }
    }

    @Override
    public boolean check(Context context) {
        var member = context.getEvent().getMember();
        return member != null && member.getRoles().stream().noneMatch(role -> role.getId().equals(roleId));
    }
}