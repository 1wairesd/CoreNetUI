package com.wairesd.discordbm.host.common.discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wairesd.discordbm.host.common.database.Database;

public class MessageManager {
    private static final String CHANNEL_MESSAGE_SEPARATOR = ":";
    private static final String GUILD_PREFIX_SEPARATOR = "_";
    private static final int EXPECTED_REFERENCE_PARTS = 2;

    private final Map<String, List<String>> globalMessageLabels = new HashMap<>();
    private Database database;

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setGlobalMessageLabel(String key, String channelId, String messageId) {
        String messageReference = createMessageReference(channelId, messageId);
        addMessageReferenceToCache(key, messageReference);
        persistMessageLabelToDatabase(key, channelId, messageId);
    }

    public String[] getMessageReference(String key) {
        String[] cachedReference = getLatestMessageReferenceFromCache(key);
        if (cachedReference != null) {
            return cachedReference;
        }

        return getMessageReferenceFromDatabase(key);
    }

    public void removeGlobalMessageLabel(String key) {
        globalMessageLabels.remove(key);
    }

    public List<String[]> getAllMessageReferencesByLabel(String key) {
        List<String> references = globalMessageLabels.get(key);
        return parseMessageReferences(references);
    }

    public void removeMessageReference(String key, String channelId, String messageId) {
        String targetReference = createMessageReference(channelId, messageId);
        removeMessageReferenceFromCache(key, targetReference);
        removeMessageReferenceFromDatabase(key, channelId, messageId);
    }

    public List<String[]> getAllMessageReferences(String labelPrefix, String guildId) {
        String fullPrefix = createGuildPrefix(guildId, labelPrefix);
        List<String[]> results = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : globalMessageLabels.entrySet()) {
            if (shouldIncludeEntry(entry.getKey(), fullPrefix, labelPrefix, guildId)) {
                addValidMessageReferences(entry.getValue(), results);
            }
        }

        return results;
    }

    public String getGlobalMessageLabel(String key) {
        List<String> references = globalMessageLabels.get(key);
        return getLatestReference(references);
    }

    private String createMessageReference(String channelId, String messageId) {
        return channelId + CHANNEL_MESSAGE_SEPARATOR + messageId;
    }

    private String createGuildPrefix(String guildId, String labelPrefix) {
        return guildId + GUILD_PREFIX_SEPARATOR + labelPrefix;
    }

    private void addMessageReferenceToCache(String key, String messageReference) {
        globalMessageLabels.computeIfAbsent(key, k -> new ArrayList<>()).add(messageReference);
    }

    private void persistMessageLabelToDatabase(String key, String channelId, String messageId) {
        if (database != null) {
            database.upsertMessageLabel(key, channelId, messageId);
        }
    }

    private String[] getLatestMessageReferenceFromCache(String key) {
        List<String> references = globalMessageLabels.get(key);
        if (references != null && !references.isEmpty()) {
            String latestReference = references.get(references.size() - 1);
            return parseMessageReference(latestReference);
        }
        return null;
    }

    private String[] getMessageReferenceFromDatabase(String key) {
        if (database != null) {
            return database.getLastMessageReference(key).join();
        }
        return null;
    }

    private List<String[]> parseMessageReferences(List<String> references) {
        List<String[]> result = new ArrayList<>();
        if (references != null) {
            for (String reference : references) {
                String[] parsed = parseMessageReference(reference);
                if (parsed != null) {
                    result.add(parsed);
                }
            }
        }
        return result;
    }

    private String[] parseMessageReference(String reference) {
        if (reference != null && reference.contains(CHANNEL_MESSAGE_SEPARATOR)) {
            return reference.split(CHANNEL_MESSAGE_SEPARATOR, EXPECTED_REFERENCE_PARTS);
        } else if (reference != null) {
            return new String[]{null, reference};
        }
        return null;
    }

    private void removeMessageReferenceFromCache(String key, String targetReference) {
        List<String> references = globalMessageLabels.get(key);
        if (references != null) {
            references.removeIf(ref -> ref.equals(targetReference));
            if (references.isEmpty()) {
                globalMessageLabels.remove(key);
            }
        }
    }

    private void removeMessageReferenceFromDatabase(String key, String channelId, String messageId) {
        if (database != null) {
            database.removeMessageReference(key, channelId, messageId);
        }
    }

    private boolean shouldIncludeEntry(String entryKey, String fullPrefix, String labelPrefix, String guildId) {
        return entryKey.equals(fullPrefix) ||
                (labelPrefix.isEmpty() && entryKey.startsWith(guildId + GUILD_PREFIX_SEPARATOR));
    }

    private void addValidMessageReferences(List<String> references, List<String[]> results) {
        for (String reference : references) {
            String[] parsed = parseMessageReference(reference);
            if (parsed != null) {
                results.add(parsed);
            }
        }
    }

    private String getLatestReference(List<String> references) {
        return (references != null && !references.isEmpty()) ?
                references.get(references.size() - 1) : null;
    }
}