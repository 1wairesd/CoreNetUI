package com.wairesd.discordbm.api.command;

import com.wairesd.discordbm.api.message.MessageSender;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Handler for checking command conditions before execution
 */
public class ConditionHandler {

    /**
     * -- GETTER --
     *  Get all conditions
     *
     * @return List of conditions
     */
    @Getter
    private final List<CommandCondition> conditions;
    private final MessageSender messageSender;
    
    public ConditionHandler(List<CommandCondition> conditions, MessageSender messageSender) {
        this.conditions = conditions;
        this.messageSender = messageSender;
    }
    
    /**
     * Check all conditions and send error message if any condition fails
     * 
     * @param options Command options
     * @param requestId Request ID
     * @return true if all conditions are met, false otherwise
     */
    public boolean checkConditions(Map<String, String> options, String requestId) {
        for (CommandCondition condition : conditions) {
            CommandConditionResult result = condition.check(options, requestId);
            if (!result.isSuccess()) {
                // Отправляем флаг ошибки на хост вместо сообщения
                sendErrorFlag(requestId, result.getErrorType(), result.getPlaceholders());
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check all conditions without sending error message
     * 
     * @param options Command options
     * @param requestId Request ID
     * @return true if all conditions are met, false otherwise
     */
    public boolean checkConditionsSilent(Map<String, String> options, String requestId) {
        for (CommandCondition condition : conditions) {
            CommandConditionResult result = condition.check(options, requestId);
            if (!result.isSuccess()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Send error flag to host instead of direct message
     */
    private void sendErrorFlag(String requestId, String errorType, Map<String, String> placeholders) {
        String errorMessage = "ERROR:" + errorType;
        if (placeholders != null && !placeholders.isEmpty()) {
            errorMessage += ":" + String.join(",", placeholders.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .toArray(String[]::new));
        }
        messageSender.sendResponse(requestId, errorMessage);
    }

} 