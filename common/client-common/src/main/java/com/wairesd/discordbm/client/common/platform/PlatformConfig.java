package com.wairesd.discordbm.client.common.platform;

import java.io.File;
import java.io.InputStream;

public interface PlatformConfig {

    File getDataFolder();
    InputStream getResource(String resourceName);
    void saveResource(String resourceName, boolean replace);
    String getPluginName();
    String getPluginVersion();
    void logInfo(String message);
    void logError(String message, Throwable throwable);
    void logWarning(String message);
} 