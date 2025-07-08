package com.wairesd.discordbm.host.common.commandbuilder.core.parser;

import com.wairesd.discordbm.host.common.commandbuilder.security.conditions.chance.ChanceCondition;
import com.wairesd.discordbm.host.common.commandbuilder.security.conditions.permissions.RoleCondition;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.conditions.CommandCondition;

import java.util.Map;

public class CommandParserCondition {
    public static CommandCondition parseCondition(Map<String, Object> conditionMap) {
        String type = (String) conditionMap.get("type");
        if (type == null) {
            throw new IllegalArgumentException("Condition type is required");
        }
        return switch (type.toLowerCase()) {
            case "permission" -> new RoleCondition(conditionMap);
            case "chance" -> new ChanceCondition(conditionMap);
            default -> throw new IllegalArgumentException("Unknown condition type: " + type);
        };
    }
}
