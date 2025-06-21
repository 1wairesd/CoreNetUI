package com.wairesd.discordbm.client.common.logging;

import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;

public class LoggerAdapter implements Logger {
    
    private final PluginLogger pluginLogger;

    public LoggerAdapter(PluginLogger pluginLogger) {
        this.pluginLogger = pluginLogger;
    }
    
    @Override
    public void info(String message) {
        pluginLogger.info(message);
    }
    
    @Override
    public void info(String message, Throwable throwable) {
        pluginLogger.info(message, throwable);
    }
    
    @Override
    public void info(String message, Object... args) {
        pluginLogger.info(message, args);
    }
    
    @Override
    public void warn(String message) {
        pluginLogger.warn(message);
    }
    
    @Override
    public void warn(String message, Throwable throwable) {
        pluginLogger.warn(message, throwable);
    }
    
    @Override
    public void warn(String message, Object... args) {
        pluginLogger.warn(message, args);
    }
    
    @Override
    public void error(String message) {
        pluginLogger.error(message);
    }
    
    @Override
    public void error(String message, Throwable throwable) {
        pluginLogger.error(message, throwable);
    }
    
    @Override
    public void error(String message, Object... args) {
        pluginLogger.error(message, args);
    }
} 