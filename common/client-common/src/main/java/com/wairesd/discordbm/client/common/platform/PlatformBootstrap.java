package com.wairesd.discordbm.client.common.platform;

import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.client.common.config.ConfigManager;

public interface PlatformBootstrap {

    void initialize();
    Platform getPlatform();
    DBMAPI getApi();
    ConfigManager getConfigManager();
} 