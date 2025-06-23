package com.wairesd.discordbm.api.command;

import java.util.Map;

public interface CommandCondition {
    String getType();
    Map<String, Object> serialize();
} 