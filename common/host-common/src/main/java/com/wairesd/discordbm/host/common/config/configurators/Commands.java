package com.wairesd.discordbm.host.common.config.configurators;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.common.config.ConfigMetaMigrator;
import com.wairesd.discordbm.host.common.commandbuilder.core.parser.CommandParserAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.conditions.CommandCondition;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.options.CommandOptions;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.commandbuilder.core.parser.CommandParserCondition;
import com.wairesd.discordbm.host.common.commandbuilder.core.parser.CommandParserFailAction;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Commands {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final String COMMANDS_FILE_NAME = "commands.yml";

    private static Path dataDirectory;
    private static volatile List<CommandStructured> customCommands = Collections.emptyList();
    private static DiscordBMHPlatformManager platformInstance;

    public static void init(Path dataDir) {
        dataDirectory = dataDir;
        loadCommands();
    }

    public static void setPlatform(DiscordBMHPlatformManager platform) {
        platformInstance = platform;
    }

    public static DiscordBMHPlatformManager getPlatform() {
        return platformInstance;
    }

    private static synchronized List<CommandStructured> loadCommands() {
        try {
            Path commandsPath = dataDirectory.resolve(COMMANDS_FILE_NAME);
            if (!Files.exists(commandsPath)) {
                createDefaultCommandsFile(commandsPath);
            }
            // ensure meta
            ConfigMetaMigrator.ensureMeta(commandsPath, "commands", 2);

            List<CommandStructured> newCommands = loadCommandsFromFile(commandsPath);
            customCommands = Collections.unmodifiableList(newCommands);
            return customCommands;
        } catch (Exception e) {
            logger.error("Error loading {}: {}", COMMANDS_FILE_NAME, e.getMessage(), e);
            return Collections.emptyList();
        }
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

    private static List<CommandStructured> loadCommandsFromFile(Path commandsPath) throws IOException {
        if (!Files.exists(commandsPath)) {
            throw new FileNotFoundException("YAML command file not found: " + commandsPath);
        }

        try (InputStream in = Files.newInputStream(commandsPath)) {
            Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
            Object loaded = yaml.load(in);

            if (!(loaded instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException("Incorrect YAML format: Root Map expected");
            }

            int version = 1;
            Object v = map.get("version");
            if (v instanceof Number) {
                version = ((Number) v).intValue();
            } else if (v instanceof String s) {
                try { version = Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) { }
            }

            Object rawCommands = map.get("commands");
            if (rawCommands == null) {
                Object nm = map.get("DiscordBM");
                if (nm instanceof Map<?, ?> nmMap) {
                    rawCommands = nmMap.get("commands");
                }
            }
            if (!(rawCommands instanceof List<?> list)) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> commandMaps = list.stream()
                    .filter(e -> e instanceof Map)
                    .map(e -> asStringObjectMap(e))
                    .toList();

            List<CommandStructured> parsed = commandMaps.stream()
                    .map(Commands::parseCommand)
                    .collect(Collectors.toList());
            logger.info("Loaded {} commands (commands.yml v{})", parsed.size(), version);
            return parsed;

        } catch (ClassCastException | IllegalArgumentException e) {
            throw new IOException("Error when parsing a YAML file: " + e.getMessage(), e);
        }
    }

    public static void reload() {
        List<CommandStructured> reloadedCommands = loadCommands();
        logger.info("{} reloaded successfully with {} commands", COMMANDS_FILE_NAME, reloadedCommands.size());
    }

    public static List<CommandStructured> getCustomCommands() {
        return customCommands != null ? customCommands : Collections.emptyList();
    }

    private static CommandStructured parseCommand(Map<String, Object> cmdData) {
        String name = getString(cmdData, "name");
        String description = getString(cmdData, "description");
        String context = getString(cmdData, "context", "both");
        String pluginName = getString(cmdData, "pluginName");

        List<CommandOptions> options = getOptions(cmdData);
        List<CommandCondition> conditions = getConditions(cmdData);
        List<CommandAction> actions = getActions(cmdData);
        List<CommandAction> failActions = CommandParserFailAction.parse(cmdData, platformInstance);
        // ephemeral is parsed per-action; command-level ephemeral currently unused
        String permission = cmdData.containsKey("permission") ? (String) cmdData.get("permission") : null;

        return new CommandStructured(
                name,
                description,
                context,
                options,
                conditions,
                actions,
                failActions,
                permission,
                pluginName
        );
    }

    private static String getString(Map<String, Object> data, String key) {
        return (String) data.get(key);
    }

    private static String getString(Map<String, Object> data, String key, String defaultValue) {
        return (String) data.getOrDefault(key, defaultValue);
    }

    private static List<CommandOptions> getOptions(Map<String, Object> cmdData) {
        return getList(cmdData, "options", Commands::createOption);
    }

    private static List<CommandCondition> getConditions(Map<String, Object> cmdData) {
        return getList(cmdData, "conditions", Commands::createCondition);
    }

    private static List<CommandAction> getActions(Map<String, Object> cmdData) {
        return getList(cmdData, "actions", Commands::createAction);
    }

    private static <T> List<T> getList(Map<String, Object> data, String key, CommandParser<T> parser) {
        Object raw = data.getOrDefault(key, Collections.emptyList());
        List<Map<String, Object>> listData;
        if (raw instanceof List<?> rl) {
            listData = rl.stream()
                    .filter(it -> it instanceof Map)
                    .map(Commands::asStringObjectMap)
                    .collect(Collectors.toList());
        } else {
            listData = Collections.emptyList();
        }
        return listData.stream()
                .map(parser::parse)
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    private static Map<String, Object> asStringObjectMap(Object e) {
        Map<?, ?> m = (Map<?, ?>) e;
        return m.entrySet().stream().collect(Collectors.toMap(
                entry -> String.valueOf(entry.getKey()),
                Map.Entry::getValue
        ));
    }

    private static CommandOptions createOption(Map<String, Object> data) {
        return new CommandOptions(
                getString(data, "name"),
                getString(data, "type"),
                getString(data, "description"),
                getBoolean(data, "required", false)
        );
    }

    private static CommandCondition createCondition(Map<String, Object> data) {
        return CommandParserCondition.parseCondition(data);
    }

    private static CommandAction createAction(Map<String, Object> data) {
        if (platformInstance == null) {
            throw new IllegalStateException("Platform instance is not set");
        }
        return CommandParserAction.parseAction(data, platformInstance);
    }

    private static boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        return (boolean) data.getOrDefault(key, defaultValue);
    }

    @FunctionalInterface
    private interface CommandParser<T> {
        T parse(Map<String, Object> data);
    }
}