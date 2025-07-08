package com.wairesd.discordbm.host.common.config.configurators;

import com.wairesd.discordbm.common.utils.color.MessageContext;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Messages {
    private static Path dataDirectory;
    private static final String MESSAGES_FILE_NAME = "messages.yml";
    public static final String DEFAULT_MESSAGE = "Message not found.";

    public static final class Keys {
        public static final String NO_PERMISSION = "no-permission";
        public static final String RELOAD_SUCCESS = "reload-success";
        public static final String COMMAND_UNAVAILABLE = "command-unavailable";
        public static final String OFFLINE_PLAYER = "offline-player";

        public static final String HELP_HEADER = "help-header";
        public static final String HELP_RELOAD = "help-reload";
        public static final String HELP_CUSTOM_COMMANDS = "help-custom-commands";
        public static final String HELP_ADDONS_COMMANDS = "help-addons-commands";
        public static final String HELP_WEBHOOK = "help-webhook";

        public static final String CUSTOM_COMMANDS_EMPTY = "custom-commands-empty";
        public static final String CUSTOM_COMMANDS_HEADER = "custom-commands-header";
        public static final String CUSTOM_COMMANDS_ENTRY = "custom-commands-entry";

        public static final String ADDONS_COMMANDS_EMPTY = "addons-commands-empty";
        public static final String ADDONS_COMMANDS_HEADER = "addons-commands-header";
        public static final String ADDONS_COMMANDS_ENTRY = "addons-commands-entry";
        public static final String ADDONS_COMMANDS_ADDON = "addons-commands-addon";
        public static final String ADDONS_COMMANDS_COMMANDS = "addons-commands-commands";

        public static final String SERVER_SELECTION_PROMPT = "server-selection-prompt";
        public static final String SERVER_SELECTION_PLACEHOLDER = "server-selection-placeholder";
        public static final String SERVER_PROCESSING = "server-processing";
        public static final String SERVER_SELECTION_TIMEOUT = "server-selection-timeout";
        public static final String SERVER_SELECTION_NO_SERVER = "server-selection-no-server";
        public static final String SERVER_SELECTION_NOT_FOUND = "server-selection-not-found";

        public static final String NO_ACTIVE_CLIENTS = "no_active_clients";
        public static final String NO_CONNECTED_CLIENTS = "no_connected_clients";

        public static final String CHANCE_FAILED = "chance-failed";
        public static final String ROLE_REQUIRED = "role-required";
    }

    public static void init(Path dataDir) {
        dataDirectory = dataDir;
        loadMessages();
    }

    private static void loadMessages() {
        try {
            com.wairesd.discordbm.common.utils.MessagesUN.load(new File(dataDirectory.toFile(), MESSAGES_FILE_NAME));
        } catch (IOException e) {
            System.err.println("Error loading messages.yml: " + e.getMessage());
        }
    }

    public static void reload() {
        loadMessages();
    }

    public static String get(String key, Object... args) {
        return com.wairesd.discordbm.common.utils.MessagesUN.get(key, args);
    }

    public static String getFormatted(String key, MessageContext context, Object... args) {
        return com.wairesd.discordbm.common.utils.MessagesUN.getFormatted(key, context, args);
    }

    public static Component getComponent(String key, MessageContext context, Object... args) {
        return com.wairesd.discordbm.common.utils.MessagesUN.getComponent(key, context, args);
    }

    public static String getMessage(String key) {
        return getMessage(key, DEFAULT_MESSAGE);
    }

    public static String getMessage(String key, String defaultValue) {
        String value = com.wairesd.discordbm.common.utils.MessagesUN.get(key);
        return (value != null && !value.equals(key)) ? value : defaultValue;
    }
}
