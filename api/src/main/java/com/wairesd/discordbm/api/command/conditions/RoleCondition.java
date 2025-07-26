package com.wairesd.discordbm.api.command.conditions;

import com.wairesd.discordbm.api.command.CommandCondition;
import com.wairesd.discordbm.api.command.CommandConditionResult;
import java.util.Map;

/**
 * Condition that checks if user has required role
 */
public class RoleCondition implements CommandCondition {
    
    private final String requiredRole;
    
    public RoleCondition(String requiredRole) {
        this.requiredRole = requiredRole;
    }
    
    @Override
    public CommandConditionResult check(Map<String, String> options, String requestId) {
        String roleId = options.get("role_id");
        if (roleId == null) {
            return CommandConditionResult.failure("ROLE_REQUIRED");
        }
        
        if (roleId.equals(requiredRole)) {
            return CommandConditionResult.success();
        }
        
        Map<String, String> placeholders = Map.of("0", requiredRole);
        return CommandConditionResult.failure("ROLE_REQUIRED", placeholders);
    }
    
    public String getRequiredRole() {
        return requiredRole;
    }
} 