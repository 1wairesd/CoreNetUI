package com.wairesd.discordbm.client.common.config;

import com.wairesd.discordbm.client.common.config.configurators.Messages;
import com.wairesd.discordbm.client.common.config.configurators.Settings;
import com.wairesd.discordbm.client.common.platform.PlatformConfig;

public class ConfigManager {
    private final PlatformConfig platformConfig;

    public ConfigManager(PlatformConfig platformConfig) {
        this.platformConfig = platformConfig;
    }

    public void loadConfigs() {
        Settings.init(platformConfig);
        Messages.init(platformConfig);
    }

    public void reloadConfigs() {
        Settings.reload(platformConfig);
        Messages.reload(platformConfig);
    }
}