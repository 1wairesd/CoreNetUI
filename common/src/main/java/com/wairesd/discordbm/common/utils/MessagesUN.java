package com.wairesd.discordbm.common.utils;

import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.common.utils.color.UniversalColorTranslator;
import net.kyori.adventure.text.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public final class MessagesUN {

    private static volatile Map<String, Object> messages = Collections.emptyMap();

    public static void load(File file) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(file)) {
            Object loaded = yaml.load(fis);
            if (loaded instanceof Map) {
                messages = (Map<String, Object>) loaded;
            } else {
                messages = Collections.emptyMap();
            }
        }
    }

    public static String get(String key, Object... args) {
        String template = getStringByKey(key);
        return template != null ? MessageFormat.format(template, args) : key;
    }

    public static String getFormatted(String key, MessageContext context, Object... args) {
        return UniversalColorTranslator.translate(get(key, args), context);
    }

    public static Component getComponent(String key, MessageContext context, Object... args) {
        return UniversalColorTranslator.translateComponent(get(key, args), context);
    }

    private static String getStringByKey(String key) {
        String[] parts = key.split("\\.");
        Object current = messages;
        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(part);
        }
        return (current instanceof String) ? (String) current : null;
    }
}
