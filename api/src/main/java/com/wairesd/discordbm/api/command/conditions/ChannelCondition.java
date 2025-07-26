package com.wairesd.discordbm.api.command.conditions;

import com.wairesd.discordbm.api.command.CommandCondition;
import com.wairesd.discordbm.api.command.CommandConditionResult;
import java.util.Map;

/**
 * Condition that checks if command is executed in allowed channel
 */
public class ChannelCondition implements CommandCondition {
    
    private final String allowedChannel;
    
    public ChannelCondition(String allowedChannel) {
        this.allowedChannel = allowedChannel;
    }
    
    @Override
    public CommandConditionResult check(Map<String, String> options, String requestId) {
        String channelId = options.get("channelId");
        if (channelId == null) {
            return CommandConditionResult.failure("INVALID_CONTEXT");
        }
        
        if (channelId.equals(allowedChannel)) {
            return CommandConditionResult.success();
        }

        Map<String, String> placeholders = Map.of("0", allowedChannel);
        return CommandConditionResult.failure("INVALID_CONTEXT", placeholders);
    }
    
    public String getAllowedChannel() {
        return allowedChannel;
    }
} 