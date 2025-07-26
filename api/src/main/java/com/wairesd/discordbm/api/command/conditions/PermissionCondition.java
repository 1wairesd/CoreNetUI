package com.wairesd.discordbm.api.command.conditions;

import com.wairesd.discordbm.api.command.CommandCondition;
import com.wairesd.discordbm.api.command.CommandConditionResult;
import java.util.Map;

/**
 * Condition that checks if user has required permission
 */
public class PermissionCondition implements CommandCondition {
    
    private final String requiredPermission;
    
    public PermissionCondition(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }
    
    @Override
    public CommandConditionResult check(Map<String, String> options, String requestId) {
        String userId = options.get("user_Id");
        if (userId == null) {
            return CommandConditionResult.failure("ROLE_REQUIRED");
        }
    
        String guildId = options.get("guild_Id");
        if (guildId == null) {
            return CommandConditionResult.failure("ROLE_REQUIRED");
        }
        
        String roleId = options.get("role_id");
        if (roleId == null) {
            return CommandConditionResult.failure("ROLE_REQUIRED");
        }
        
        if (roleId.equals(requiredPermission)) {
            return CommandConditionResult.success();
        }
        
        Map<String, String> placeholders = Map.of("0", requiredPermission);
        return CommandConditionResult.failure("ROLE_REQUIRED", placeholders);
    }
    
    public String getRequiredPermission() {
        return requiredPermission;
    }
} 
