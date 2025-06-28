package com.wairesd.discordbm.client.common.platform;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.client.common.config.ConfigManager;

public interface PlatformBootstrap {

    void initialize();
    Platform getPlatform();
    DiscordBMAPI getApi();
    ConfigManager getConfigManager();
} 