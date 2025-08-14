package com.wairesd.discordbm.common.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigMetaMigrator {
    private ConfigMetaMigrator() {}

    public static void ensureMeta(Path file, String type, int version) throws IOException {
        if (!Files.exists(file)) return;

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Representer representer = new Representer(options);
        representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(representer, options);

        Map<String, Object> root;
        try (InputStream in = Files.newInputStream(file)) {
            Object loaded = yaml.load(in);
            if (loaded instanceof Map<?, ?> m) {
                @SuppressWarnings("unchecked")
                Map<String, Object> casted = (Map<String, Object>) m;
                root = new LinkedHashMap<>(casted);
            } else {
                root = new LinkedHashMap<>();
            }
        }

        boolean changed = false;
        Object metaObj = root.get("config");
        Map<String, Object> meta;
        if (metaObj instanceof Map<?, ?> m) {
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) m;
            meta = new LinkedHashMap<>(casted);
        } else {
            meta = new LinkedHashMap<>();
            changed = true;
        }

        Object existingVersion = meta.get("version");
        if (!(existingVersion instanceof Number)) {
            meta.put("version", version);
            changed = true;
        }

        Object existingType = meta.get("type");
        if (!(existingType instanceof String)) {
            meta.put("type", type);
            changed = true;
        }

        if (!changed) return;

        Map<String, Object> newRoot = new LinkedHashMap<>();
        newRoot.put("config", meta);
        for (Map.Entry<String, Object> entry : root.entrySet()) {
            if (!entry.getKey().equals("config")) {
                newRoot.put(entry.getKey(), entry.getValue());
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            yaml.dump(newRoot, writer);
        }
    }
}
