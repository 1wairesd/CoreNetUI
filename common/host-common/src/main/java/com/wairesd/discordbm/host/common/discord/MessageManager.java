package com.wairesd.discordbm.host.common.discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {
    private final Map<String, String> globalMessageLabels = new HashMap<>();

    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        globalMessageLabels.put(key, channelId + ":" + messageId);
    }

    public String[] getMessageReference(String key) {
        String value = globalMessageLabels.get(key);
        if (value == null) return null;
        return value.contains(":") ? value.split(":", 2) : new String[]{null, value};
    }

    public void removeGlobalMessageLabel(String key) {
        globalMessageLabels.remove(key);
    }

    public List<String[]> getAllMessageReferences(String labelPrefix, String guildId) {
        String fullPrefix = guildId + "_" + labelPrefix;
        List<String[]> results = new ArrayList<>();

        for (Map.Entry<String, String> entry : globalMessageLabels.entrySet()) {
            if (entry.getKey().equals(fullPrefix) || 
                    (labelPrefix.isEmpty() && entry.getKey().startsWith(guildId + "_"))) {
                
                String value = entry.getValue();
                if (value != null && value.contains(":")) {
                    String[] parts = value.split(":", 2);
                    results.add(parts);
                }
            }
        }
        
        return results;
    }

    public String getGlobalMessageLabel(String key) {
        return globalMessageLabels.get(key);
    }

    public Map<String, String> getGlobalMessageLabels() {
        return globalMessageLabels;
    }
} 