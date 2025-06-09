package com.wairesd.discordbm.common.utils.color.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VelocityColorTranslator {
    private VelocityColorTranslator() {}

    public static String translate(String message) {
        if (message == null) return "";
        message = message.replace('&', '§');
        Pattern hexPattern = Pattern.compile("§#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        
        matcher.appendTail(sb);
        Pattern rawHex = Pattern.compile("(?<!§)#([A-Fa-f0-9]{6})");
        matcher = rawHex.matcher(sb.toString());
        sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
} 