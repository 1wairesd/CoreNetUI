package com.wairesd.discordbm.client.common.placeholders;

import com.wairesd.discordbm.client.common.platform.PlatformPlaceholder;

import java.util.*;

public class PlaceholderService {

    private final PlatformPlaceholder platformService;

    public PlaceholderService(PlatformPlaceholder platformService) {
        this.platformService = platformService;
    }

    public boolean checkIfCanHandle(String playerName, List<String> placeholders) {
        return platformService.checkIfCanHandle(playerName, placeholders);
    }

    public Map<String, String> getPlaceholderValues(String playerName, List<String> placeholders) {
        return platformService.getPlaceholderValues(playerName, placeholders);
    }
}
