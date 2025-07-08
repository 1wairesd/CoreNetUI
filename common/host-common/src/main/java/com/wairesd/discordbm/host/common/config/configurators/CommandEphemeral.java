package com.wairesd.discordbm.host.common.config.configurators;

import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandEphemeral {
    private static final String CONFIG_FILE_NAME = "commands-ephemeral.yml";
    private static Path dataDirectory;
    private static final Map<String, Boolean> commandEphemeralMap = new HashMap<>();
    private static final Map<String, Map<String, Boolean>> clientEphemeralRules = new ConcurrentHashMap<>();

    public static void init(Path dataDir) {
        dataDirectory = dataDir;
        loadConfig();
    }

    private static void loadConfig() {
        commandEphemeralMap.clear();
        File file = new File(dataDirectory.toFile(), CONFIG_FILE_NAME);
        if (!file.exists()) {
            try {
                createDefaultConfigFile(file);
            } catch (IOException e) {
                System.err.println("Error creating default " + CONFIG_FILE_NAME + ": " + e.getMessage());
                return;
            }
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(fis);
            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String command = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Boolean) {
                        commandEphemeralMap.put(command, (Boolean) value);
                    } else if (value instanceof Map) {
                        Map<String, Object> args = (Map<String, Object>) value;
                        for (Map.Entry<String, Object> argEntry : args.entrySet()) {
                            String key = command + " " + argEntry.getKey();
                            commandEphemeralMap.put(key, Boolean.parseBoolean(argEntry.getValue().toString()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading commands-ephemeral.yml: " + e.getMessage());
        }
    }

    private static void createDefaultConfigFile(File file) throws IOException {
        file.getParentFile().mkdirs();
        try (InputStream in = CommandEphemeral.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (in != null) {
                Files.copy(in, file.toPath());
            } else {
                System.err.println(CONFIG_FILE_NAME + " not found in resources!");
            }
        }
    }

    public static void reload() {
        loadConfig();
    }

    public static Boolean getEphemeralForCommand(String command, Map<String, String> options) {
        StringBuilder sb = new StringBuilder(command);
        for (Map.Entry<String, String> entry : options.entrySet()) {
            sb.append(" ").append(entry.getKey()).append(":").append(entry.getValue());
        }
        String specificKey = sb.toString();
        if (!options.isEmpty()) {
            for (String key : options.keySet()) {
                String k = command + "_args " + key;
                if (commandEphemeralMap.containsKey(k)) {
                    return commandEphemeralMap.get(k);
                }
            }
            String argsKey = command + "_args";
            if (commandEphemeralMap.containsKey(argsKey)) {
                return commandEphemeralMap.get(argsKey);
            }
        }
        if (commandEphemeralMap.containsKey(command)) {
            return commandEphemeralMap.get(command);
        }
        if (!options.isEmpty()) {
            for (String key : options.keySet()) {
                String k = command + "_args " + key;
                for (var clientRules : clientEphemeralRules.values()) {
                    if (clientRules.containsKey(k)) {
                        return clientRules.get(k);
                    }
                }
            }
            String argsKey = command + "_args";
            for (var clientRules : clientEphemeralRules.values()) {
                if (clientRules.containsKey(argsKey)) {
                    return clientRules.get(argsKey);
                }
            }
        }
        for (var clientRules : clientEphemeralRules.values()) {
            if (clientRules.containsKey(command)) {
                return clientRules.get(command);
            }
        }
        return false;
    }

    public static void addOrUpdateClientRule(String clientId, String ruleKey, boolean ephemeral) {
        clientEphemeralRules.computeIfAbsent(clientId, k -> new ConcurrentHashMap<>()).put(ruleKey, ephemeral);
    }

    public static void removeAllClientRules(String clientId) {
        if (clientId == null) return;
        clientEphemeralRules.remove(clientId);
    }

    public static void removeAllPluginRules(String pluginName) {
        for (var entry : clientEphemeralRules.entrySet()) {
            entry.getValue().keySet().removeIf(key -> key.startsWith(pluginName + " ") || key.equals(pluginName));
        }
    }
} 