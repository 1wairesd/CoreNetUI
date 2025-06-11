package com.wairesd.discordbm.common.utils.logging;

public interface PluginLogger {

    void info(String message);
    void info(String message, Throwable throwable);
    void info(String message, Object... args);

    void warn(String message);
    void warn(String message, Throwable throwable);
    void warn(String message, Object... args);

    void error(String message);
    void error(String message, Throwable throwable);
    void error(String message, Object... args);
}
