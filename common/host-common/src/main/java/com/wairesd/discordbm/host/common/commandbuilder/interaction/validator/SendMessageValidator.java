package com.wairesd.discordbm.host.common.commandbuilder.interaction.validator;

import java.util.Map;

public class SendMessageValidator {
    public static void validate(Map<String, Object> properties) {
        Object messageObj = properties.get("message");
        String responseType = (String) properties.getOrDefault("response_type", "REPLY");
        boolean hasMessage;
        if (responseType.equalsIgnoreCase("RANDOM_REPLY")) {
            hasMessage = messageObj instanceof java.util.List && !((java.util.List<?>) messageObj).isEmpty();
        } else {
            hasMessage = messageObj instanceof String && !((String) messageObj).isEmpty();
        }
        boolean hasEmbed = properties.containsKey("embed");

        if (!hasMessage && !hasEmbed) {
            throw new IllegalArgumentException("Message or embed is required for SendMessageAction");
        }

        String targetId = (String) properties.get("target_id");
        if ((responseType.equalsIgnoreCase("SPECIFIC_CHANNEL") || responseType.equalsIgnoreCase("EDIT_MESSAGE"))
                && (targetId == null || targetId.isEmpty())) {
            throw new IllegalArgumentException("target_id is required for " + responseType);
        }
    }
}
