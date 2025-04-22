package com.wairesd.discordbm.common.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private static final boolean BUKKIT_AVAILABLE = isBukkitPresent();

    private static boolean isBukkitPresent() {
        try {
            Class.forName("org.bukkit.ChatColor");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Component parseComponent(String message) {
        return LEGACY.deserialize(parseString(message));
    }

    public static String parseString(String message) {
        return BUKKIT_AVAILABLE
                ? org.bukkit.ChatColor.translateAlternateColorCodes('&', message)
                : message;
    }
}