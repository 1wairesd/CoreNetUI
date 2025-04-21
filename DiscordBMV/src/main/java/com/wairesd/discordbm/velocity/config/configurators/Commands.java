package com.wairesd.discordbm.velocity.config.configurators;

import com.wairesd.discordbm.velocity.commands.custom.actions.ButtonAction;
import com.wairesd.discordbm.velocity.commands.custom.actions.SendMessageAction;
import com.wairesd.discordbm.velocity.commands.custom.conditions.PermissionCondition;
import com.wairesd.discordbm.velocity.commands.custom.models.CommandAction;
import com.wairesd.discordbm.velocity.commands.custom.models.CommandCondition;
import com.wairesd.discordbm.velocity.commands.custom.models.CommandOption;
import com.wairesd.discordbm.velocity.commands.custom.models.CustomCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Commands {
    private static final Logger logger = LoggerFactory.getLogger(Commands.class);
    private static final String COMMANDS_FILE_NAME = "commands.yml";

    private static Path dataDirectory;
    private static List<CustomCommand> customCommands;

    public static void init(Path dataDir) {
        dataDirectory = dataDir;
        loadCommands();
    }

    private static void loadCommands() {
        CompletableFuture.runAsync(() -> {
            try {
                Path commandsPath = dataDirectory.resolve(COMMANDS_FILE_NAME);
                if (!Files.exists(commandsPath)) {
                    createDefaultCommandsFile(commandsPath);
                }

                customCommands = loadCommandsFromFile(commandsPath);
                logger.info("{} loaded successfully with {} commands", COMMANDS_FILE_NAME, customCommands.size());
            } catch (Exception e) {
                logger.error("Error loading {}: {}", COMMANDS_FILE_NAME, e.getMessage(), e);
            }
        });
    }

    private static void createDefaultCommandsFile(Path commandsPath) throws IOException {
        Files.createDirectories(dataDirectory);
        try (InputStream in = Commands.class.getClassLoader().getResourceAsStream(COMMANDS_FILE_NAME)) {
            if (in != null) {
                Files.copy(in, commandsPath);
            } else {
                logger.error("{} not found in resources!", COMMANDS_FILE_NAME);
            }
        }
    }

    private static List<CustomCommand> loadCommandsFromFile(Path commandsPath) throws IOException {
        try (InputStream in = Files.newInputStream(commandsPath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(in);
            List<Map<String, Object>> commandsList = (List<Map<String, Object>>) data.getOrDefault("commands", Collections.emptyList());
            return commandsList.stream()
                    .map(Commands::parseCommand)
                    .collect(Collectors.toList());
        }
    }

    public static void reload() {
        loadCommands();
    }

    public static List<CustomCommand> getCustomCommands() {
        return customCommands != null ? customCommands : Collections.emptyList();
    }

    private static CustomCommand parseCommand(Map<String, Object> cmdData) {
        String name = getString(cmdData, "name");
        String description = getString(cmdData, "description");
        String context = getString(cmdData, "context", "both");

        List<CommandOption> options = getOptions(cmdData);
        List<CommandCondition> conditions = getConditions(cmdData);
        List<CommandAction> actions = getActions(cmdData);

        return new CustomCommand(
                name,
                description,
                context,
                options,
                conditions,
                actions
        );
    }

    private static String getString(Map<String, Object> data, String key) {
        return (String) data.get(key);
    }

    private static String getString(Map<String, Object> data, String key, String defaultValue) {
        return (String) data.getOrDefault(key, defaultValue);
    }

    private static List<CommandOption> getOptions(Map<String, Object> cmdData) {
        return getList(cmdData, "options", Commands::createOption);
    }

    private static List<CommandCondition> getConditions(Map<String, Object> cmdData) {
        return getList(cmdData, "conditions", Commands::createCondition);
    }

    private static List<CommandAction> getActions(Map<String, Object> cmdData) {
        return getList(cmdData, "actions", Commands::createAction);
    }

    private static <T> List<T> getList(Map<String, Object> data, String key, CommandParser<T> parser) {
        List<Map<String, Object>> listData = (List<Map<String, Object>>) data.getOrDefault(key, Collections.emptyList());
        return listData.stream()
                .map(parser::parse)
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    private static CommandOption createOption(Map<String, Object> data) {
        return new CommandOption(
                getString(data, "name"),
                getString(data, "type"),
                getString(data, "description"),
                getBoolean(data, "required", false)
        );
    }

    private static CommandCondition createCondition(Map<String, Object> data) {
        String type = getString(data, "type");
        return switch (type) {
            case "permission" -> new PermissionCondition(data);
            default -> null;
        };
    }

    private static CommandAction createAction(Map<String, Object> data) {
        String type = getString(data, "type");
        return switch (type) {
            case "send_message" -> new SendMessageAction(data);
            case "button" -> new ButtonAction(data);
            default -> null;
        };
    }

    private static boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        return (boolean) data.getOrDefault(key, defaultValue);
    }

    @FunctionalInterface
    private interface CommandParser<T> {
        T parse(Map<String, Object> data);
    }
}
