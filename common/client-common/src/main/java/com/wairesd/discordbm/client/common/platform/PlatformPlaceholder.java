package com.wairesd.discordbm.client.common.platform;

import java.util.List;
import java.util.Map;

public interface PlatformPlaceholder {
    boolean checkIfCanHandle(String playerName, List<String> placeholders);
    Map<String, String> getPlaceholderValues(String playerName, List<String> placeholders);
} 