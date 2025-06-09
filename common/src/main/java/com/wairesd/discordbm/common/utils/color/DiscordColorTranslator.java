package com.wairesd.discordbm.common.utils.color;

public class DiscordColorTranslator {
    public static String translate(String message) {
        if (message == null) return "";
        return message.replaceAll("[&§][0-9a-fk-orA-FK-OR]", "");
    }
} 