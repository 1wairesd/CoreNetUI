package com.wairesd.discordbm.api.command;

import java.util.Map;

/**
 * Interface for command conditions that can be checked before command execution
 */
public interface CommandCondition {
    
    /**
     * Check if the condition is met
     * 
     * @param options Command options
     * @param requestId Request ID
     * @return CommandConditionResult with success status and error information
     */
    CommandConditionResult check(Map<String, String> options, String requestId);
} 