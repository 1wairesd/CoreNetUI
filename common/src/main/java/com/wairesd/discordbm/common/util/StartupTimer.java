package com.wairesd.discordbm.common.util;

public class StartupTimer {
    private long startTime;
    private long endTime;
    private boolean running;

    public void start() {
        if (running) return;
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        if (!running) return;
        endTime = System.currentTimeMillis();
        running = false;
    }

    public long getElapsedMillis() {
        return running ? System.currentTimeMillis() - startTime : endTime - startTime;
    }

    public void printElapsed() {
        System.out.println("Successfully loaded in " + getElapsedMillis() + " ms");
    }

    public void reset() {
        startTime = 0;
        endTime = 0;
        running = false;
    }
}
