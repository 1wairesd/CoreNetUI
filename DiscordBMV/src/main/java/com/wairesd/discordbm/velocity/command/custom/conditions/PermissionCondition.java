package com.wairesd.discordbm.velocity.command.custom.conditions;

import com.wairesd.discordbm.velocity.command.custom.models.CommandCondition;
import com.wairesd.discordbm.velocity.command.custom.models.Context;
import java.util.Map;

public class PermissionCondition implements CommandCondition {
    private final String requiredRole;

    public PermissionCondition(Map<String, Object> properties) {
        this.requiredRole = (String) properties.getOrDefault("role", "");
        if (this.requiredRole.isEmpty()) {
            throw new IllegalArgumentException("Role property is required for PermissionCondition");
        }
    }

    @Override
    public boolean check(Context context) {
        if (context == null || context.getEvent() == null) {
            return false;
        }
        var member = context.getEvent().getMember();
        return member != null && member.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(requiredRole));
    }
}