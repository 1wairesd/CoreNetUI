package com.wairesd.discordbm.velocity.commands.custom.actions;

import java.util.concurrent.*;

public class ButtonActionRegistry {
    private static class ButtonData {
        private final String message;
        private final long expirationTime;

        public ButtonData(String message, long expirationTime) {
            this.message = message;
            this.expirationTime = expirationTime;
        }

        public String getMessage() {
            return message;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expirationTime;
        }
    }

    private static final ConcurrentMap<String, ButtonData> buttonDataMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        scheduler.scheduleAtFixedRate(ButtonActionRegistry::cleanupExpiredEntries, 1, 1, TimeUnit.MINUTES);
    }

    public static void register(String id, String message, long durationMillis) {
        if (id == null || id.isEmpty() || message == null) {
            throw new IllegalArgumentException("ID and message cannot be null or empty");
        }

        long expirationTime = System.currentTimeMillis() + durationMillis;
        buttonDataMap.put(id, new ButtonData(message, expirationTime));
    }

    public static String getMessage(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        ButtonData data = buttonDataMap.remove(id);
        return (data != null && !data.isExpired()) ? data.getMessage() : null;
    }

    private static void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        buttonDataMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public static void shutdown() {
        scheduler.shutdown();
    }
}
