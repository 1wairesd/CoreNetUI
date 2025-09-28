package com.wairesd.discordbm.api.command;

import java.util.Map;
import java.util.HashMap;

/**
 * Result of a command condition check
 */
public class CommandConditionResult {
    private final boolean success;
    private final String errorType;
    private final Map<String, String> placeholders;
    
    private CommandConditionResult(boolean success, String errorType, Map<String, String> placeholders) {
        this.success = success;
        this.errorType = errorType;
        this.placeholders = placeholders;
    }
    
    /**
     * Create a successful result
     */
    public static CommandConditionResult success() {
        return new CommandConditionResult(true, null, null);
    }
    
    /**
     * Create a failure result with error type
     */
    public static CommandConditionResult failure(String errorType) {
        return new CommandConditionResult(false, errorType, new HashMap<>());
    }
    
    /**
     * Create a failure result with error type and placeholders
     */
    public static CommandConditionResult failure(String errorType, Map<String, String> placeholders) {
        return new CommandConditionResult(false, errorType, placeholders);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public Map<String, String> getPlaceholders() {
        return placeholders;
    }
} 