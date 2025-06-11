package com.wairesd.discordbm.velocity.commandbuilder.components.buttons.model;

public class ButtonData {
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