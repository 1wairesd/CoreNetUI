package com.wairesd.discordbm.common.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;

public class StartupTimer {
    private long startTime = 0;
    private long endTime = 0;
    private boolean running = false;
    private final PluginLogger logger;

    public StartupTimer(PluginLogger logger) {
        this.logger = logger;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        endTime = System.currentTimeMillis();
        running = false;
    }

    public long getElapsedMillis() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return endTime - startTime;
        }
    }

    public void printElapsed() {
        logger.info("Successfully loaded in " + getElapsedMillis() + " ms");
    }
}
