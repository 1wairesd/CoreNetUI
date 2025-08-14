package com.wairesd.discordbm.host.common.discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wairesd.discordbm.host.common.database.Database;

public class MessageManager {
    private final Map<String, List<String>> globalMessageLabels = new HashMap<>();
    private Database database;

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        globalMessageLabels.computeIfAbsent(key, k -> new ArrayList<>()).add(channelId + ":" + messageId);
        if (database != null) database.upsertMessageLabel(key, channelId, messageId);
    }

    public String[] getMessageReference(String key) {
        List<String> refs = globalMessageLabels.get(key);
        if (refs != null && !refs.isEmpty()) {
            String value = refs.get(refs.size() - 1);
            return value.contains(":") ? value.split(":", 2) : new String[]{null, value};
        }
        if (database != null) {
            return database.getLastMessageReference(key).join();
        }
        return null;
    }

    public void removeGlobalMessageLabel(String key) {
        globalMessageLabels.remove(key);
    }

    public List<String[]> getAllMessageReferencesByLabel(String key) {
        List<String> refs = globalMessageLabels.get(key);
        List<String[]> result = new ArrayList<>();
        if (refs != null) {
            for (String value : refs) {
                if (value.contains(":")) {
                    result.add(value.split(":", 2));
                }
            }
        }
        return result;
    }

    public void removeMessageReference(String key, String channelId, String messageId) {
        List<String> refs = globalMessageLabels.get(key);
        if (refs != null) {
            refs.removeIf(ref -> ref.equals(channelId + ":" + messageId));
            if (refs.isEmpty()) {
                globalMessageLabels.remove(key);
            }
        }
        if (database != null) database.removeMessageReference(key, channelId, messageId);
    }

    public List<String[]> getAllMessageReferences(String labelPrefix, String guildId) {
        String fullPrefix = guildId + "_" + labelPrefix;
        List<String[]> results = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : globalMessageLabels.entrySet()) {
            if (entry.getKey().equals(fullPrefix) || 
                    (labelPrefix.isEmpty() && entry.getKey().startsWith(guildId + "_"))) {
                for (String value : entry.getValue()) {
                    if (value != null && value.contains(":")) {
                        String[] parts = value.split(":", 2);
                        results.add(parts);
                    }
                }
            }
        }
        return results;
    }

    public String getGlobalMessageLabel(String key) {
        List<String> refs = globalMessageLabels.get(key);
        return (refs != null && !refs.isEmpty()) ? refs.get(refs.size() - 1) : null;
    }
} 