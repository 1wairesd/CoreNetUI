package com.wairesd.discordbm.common.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final LegacyComponentSerializer LEGACY_AMP = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .build();

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private static final boolean BUKKIT_AVAILABLE = isBukkitPresent();

    private static final Pattern AMP_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern RAW_HEX_PATTERN = Pattern.compile("(?<!<)#([A-Fa-f0-9]{6})");

    private static boolean isBukkitPresent() {
        try {
            Class.forName("org.bukkit.ChatColor");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Component parseComponent(String message) {
        if (message == null || message.isEmpty()) return Component.empty();

        String cleaned = normalizeHexCodes(message);

        try {
            return MINI.deserialize(cleaned);
        } catch (Exception ignored) {
        }

        try {
            if (cleaned.contains("&")) return LEGACY_AMP.deserialize(cleaned);
            if (cleaned.contains("§")) return LEGACY_SECTION.deserialize(cleaned);
        } catch (Exception ignored) {
        }

        return Component.text(cleaned);
    }

    public static String parseString(String message) {
        if (message == null || message.isEmpty()) return "";

        if (BUKKIT_AVAILABLE) {
            return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        }

        return message;
    }

    private static String normalizeHexCodes(String message) {
        if (message == null) return "";

        Matcher ampMatcher = AMP_HEX_PATTERN.matcher(message);
        message = ampMatcher.replaceAll("<#$1>");

        Matcher rawMatcher = RAW_HEX_PATTERN.matcher(message);
        message = rawMatcher.replaceAll("<#$1>");

        return message;
    }
}
