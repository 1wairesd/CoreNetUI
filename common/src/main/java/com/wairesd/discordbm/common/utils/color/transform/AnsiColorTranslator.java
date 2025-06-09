package com.wairesd.discordbm.common.utils.color.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AnsiColorTranslator {
    private static final Map<Character, String> ANSI_COLORS = new HashMap<>();
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_UNDERLINE = "\u001B[4m";
    private static final String ANSI_ITALIC = "\u001B[3m";

    static {
        ANSI_COLORS.put('0', "\u001B[30m");
        ANSI_COLORS.put('1', "\u001B[34m"); // Dark Blue
        ANSI_COLORS.put('2', "\u001B[32m"); // Dark Green
        ANSI_COLORS.put('3', "\u001B[36m"); // Dark Aqua
        ANSI_COLORS.put('4', "\u001B[31m"); // Dark Red
        ANSI_COLORS.put('5', "\u001B[35m"); // Dark Purple
        ANSI_COLORS.put('6', "\u001B[33m"); // Gold
        ANSI_COLORS.put('7', "\u001B[37m"); // Gray
        ANSI_COLORS.put('8', "\u001B[90m"); // Dark Gray
        ANSI_COLORS.put('9', "\u001B[94m"); // Blue
        ANSI_COLORS.put('a', "\u001B[92m"); // Green
        ANSI_COLORS.put('b', "\u001B[96m"); // Aqua
        ANSI_COLORS.put('c', "\u001B[91m"); // Red
        ANSI_COLORS.put('d', "\u001B[95m"); // Light Purple
        ANSI_COLORS.put('e', "\u001B[93m"); // Yellow
        ANSI_COLORS.put('f', "\u001B[97m"); // White
    }

    private AnsiColorTranslator() {}

    public static String translate(String message) {
        if (message == null) return "";
        message = message.replace('&', '§');
        message = replaceHexColors(message);
        StringBuilder result = new StringBuilder();
        boolean bold = false, underline = false, italic = false;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '§' && i + 1 < message.length()) {
                char code = Character.toLowerCase(message.charAt(i + 1));
                i++;
                switch (code) {
                    case 'l': result.append(ANSI_BOLD); bold = true; break;
                    case 'n': result.append(ANSI_UNDERLINE); underline = true; break;
                    case 'o': result.append(ANSI_ITALIC); italic = true; break;
                    case 'r': result.append(ANSI_RESET); bold = underline = italic = false; break;
                    default:
                        String ansi = ANSI_COLORS.get(code);
                        if (ansi != null) {
                            result.append(ansi);
                        }
                        break;
                }
            } else {
                result.append(c);
            }
        }
        result.append(ANSI_RESET);
        return result.toString();
    }

    private static String replaceHexColors(String message) {
        Pattern hexPattern = Pattern.compile("§x(§[A-Fa-f0-9]){6}");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String seq = matcher.group();
            String hex = seq.replaceAll("§x|§", "");
            if (hex.length() == 6) {
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                String ansi = String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
                matcher.appendReplacement(sb, ansi);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
} 