package com.wairesd.discordbm.velocity.command.custom.actions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
            return System.currentTimeMillis() > expirationTime;
        }
    }

    private static final ConcurrentMap<String, ButtonData> buttonDataMap = new ConcurrentHashMap<>();

    public static void register(String id, String message, long durationMillis) {
        long expirationTime = System.currentTimeMillis() + durationMillis;
        buttonDataMap.put(id, new ButtonData(message, expirationTime));
    }

    public static String getMessage(String id) {
        ButtonData data = buttonDataMap.remove(id);
        if (data != null && !data.isExpired()) {
            return data.getMessage();
        }
        return null;
    }
}