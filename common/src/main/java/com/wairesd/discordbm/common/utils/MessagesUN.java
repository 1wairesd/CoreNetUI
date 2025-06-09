package com.wairesd.discordbm.common.utils;

import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.common.utils.color.UniversalColorTranslator;
import net.kyori.adventure.text.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class MessagesUN {
    private static Map<String, Object> messages = new HashMap<>();

    public static void load(File file) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(file)) {
            messages = yaml.load(fis);
        }
    }

    public static String get(String key, Object... args) {
        String template = getStringByKey(key);
        return MessageFormat.format(template, args);
    }

    public static String getFormatted(String key, MessageContext context, Object... args) {
        String raw = get(key, args);
        return UniversalColorTranslator.translate(raw, context);
    }

    public static Component getComponent(String key, MessageContext context, Object... args) {
        String raw = get(key, args);
        return UniversalColorTranslator.translateComponent(raw, context);
    }

    private static String getStringByKey(String key) {
        String[] parts = key.split("\\.");
        Object current = messages;
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return key;
            }
        }
        return current instanceof String ? (String) current : key;
    }
} 