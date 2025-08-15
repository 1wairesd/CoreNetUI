package com.wairesd.discordbm.host.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.event.plugin.DiscordBMReloadEvent;
import com.wairesd.discordbm.host.common.models.command.HostCommandRegistration;
import com.wairesd.discordbm.host.common.config.ConfigManager;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.manager.WebhookManager;
import com.wairesd.discordbm.host.common.scheduler.WebhookScheduler;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.host.common.utils.ClientInfo;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.DumperOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.GZIPOutputStream;
import okhttp3.*;

public class HostCommandService {

    private static final String BYTEBIN_URL = "https://bytebin.lucko.me/";

    public static String reload(Path dataDirectory, DiscordBMHPlatformManager platformManager) {
        shutdownAndRestartWebhookScheduler();
        ConfigManager.ConfigureReload();

        if (platformManager != null) {
            updatePlatformManager(platformManager);
        }

        postReloadEvents();
        return Messages.get(Messages.Keys.RELOAD_SUCCESS);
    }

    private static void shutdownAndRestartWebhookScheduler() {
        WebhookScheduler.shutdown();
        WebhookScheduler.start();
    }

    private static void updatePlatformManager(DiscordBMHPlatformManager platformManager) {
        if (platformManager.getNettyServer() != null) {
            platformManager.updateActivity();
            platformManager.getCommandManager().loadAndRegisterCommands();
        }
    }

    private static void postReloadEvents() {
        DBMAPI api = DBMAPI.getInstance();
        api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.FULL));
        api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.CONFIG));
        api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.COMMANDS));
        api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.NETWORK));
    }

    public static String toggleWebhook(Path dataDirectory, String webhookName, boolean enable) {
        WebhookScheduler.shutdown();
        String result = WebhookManager.handleWebhookToggle(dataDirectory, webhookName, enable);
        WebhookScheduler.start();
        return result;
    }

    public static String listClients(DiscordBMHPlatformManager platformManager) {
        NettyServer nettyServer = platformManager.getNettyServer();
        if (nettyServer == null) return Messages.get(Messages.Keys.NO_ACTIVE_CLIENTS);

        List<ClientInfo> clients = nettyServer.getActiveClientsInfo();
        if (clients.isEmpty()) return Messages.get(Messages.Keys.NO_CONNECTED_CLIENTS);

        StringBuilder sb = new StringBuilder();
        sb.append("Clients (").append(clients.size()).append("):");
        clients.forEach(client -> sb.append("\n- ")
                .append(client.name)
                .append(" (").append(client.ip).append(":").append(client.port).append(") time: ")
                .append(formatUptime(client.uptimeMillis)));
        return sb.toString();
    }

    private static String formatUptime(long millis) {
        long days = millis / (1000 * 60 * 60 * 24);
        long hours = (millis / (1000 * 60 * 60)) % 24;
        long minutes = (millis / (1000 * 60)) % 60;
        long seconds = (millis / 1000) % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");
        return sb.toString();
    }

    public static String getHelp(MessageContext context) {
        return joinMessages(
                Messages.get(Messages.Keys.HELP_HEADER, context),
                Messages.get(Messages.Keys.HELP_RELOAD, context),
                Messages.get(Messages.Keys.HELP_WEBHOOK, context),
                Messages.get(Messages.Keys.HELP_EDITOR, context),
                Messages.get(Messages.Keys.HELP_APPLYEDITS, context),
                Messages.get(Messages.Keys.HELP_CUSTOM_COMMANDS, context),
                Messages.get(Messages.Keys.HELP_ADDONS_COMMANDS, context)
        );
    }

    private static String joinMessages(String... messages) {
        StringJoiner joiner = new StringJoiner("\n");
        for (String msg : messages) joiner.add(msg);
        return joiner.toString();
    }

    public static String getCustomCommands(MessageContext context) {
        List<CommandStructured> customCommands = com.wairesd.discordbm.host.common.config.configurators.Commands.getCustomCommands();
        if (customCommands.isEmpty()) return Messages.get(Messages.Keys.CUSTOM_COMMANDS_EMPTY, context);

        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(Messages.Keys.CUSTOM_COMMANDS_HEADER, customCommands.size()));
        customCommands.forEach(cmd -> sb.append("\n").append(Messages.get(Messages.Keys.CUSTOM_COMMANDS_ENTRY, cmd.getName())));
        return sb.toString();
    }

    public static String getAddonCommands(DiscordBMHPlatformManager platformManager, MessageContext context) {
        if (platformManager == null || platformManager.getNettyServer() == null)
            return Messages.get(Messages.Keys.ADDONS_COMMANDS_EMPTY, context);

        Map<String, Map<String, List<String>>> grouped = groupAddonCommands(platformManager);
        if (grouped.isEmpty()) return Messages.get(Messages.Keys.ADDONS_COMMANDS_EMPTY, context);

        return formatAddonCommands(grouped);
    }

    private static Map<String, Map<String, List<String>>> groupAddonCommands(DiscordBMHPlatformManager platformManager) {
        NettyServer nettyServer = platformManager.getNettyServer();
        Map<String, Map<String, List<String>>> grouped = new HashMap<>();
        Set<String> customNames = new HashSet<>();
        com.wairesd.discordbm.host.common.config.configurators.Commands.getCustomCommands()
                .forEach(cmd -> customNames.add(cmd.getName()));

        nettyServer.getCommandToServers().forEach((command, servers) -> {
            if (customNames.contains(command)) return;
            String plugin = Optional.ofNullable(nettyServer.getCommandDefinitions().get(command))
                    .map(def -> def.pluginName() != null && !def.pluginName().isEmpty() ? def.pluginName() : nettyServer.getPluginForCommand(command))
                    .orElse(nettyServer.getPluginForCommand(command));

            servers.forEach(serverInfo -> grouped.computeIfAbsent(serverInfo.serverName(), k -> new HashMap<>())
                    .computeIfAbsent(plugin, k -> new ArrayList<>())
                    .add(command));
        });

        DBMAPI api = DBMAPI.getInstance();
        if (api != null && api.getCommandRegistration() instanceof HostCommandRegistration hostReg) {
            hostReg.getRegisteredCommands().forEach(cmd -> {
                String plugin = cmd.getPluginName() != null ? cmd.getPluginName() : "host";
                grouped.computeIfAbsent("host", k -> new HashMap<>())
                        .computeIfAbsent(plugin, k -> new ArrayList<>())
                        .add(cmd.getName());
            });
        }

        return grouped;
    }

    private static String formatAddonCommands(Map<String, Map<String, List<String>>> grouped) {
        StringBuilder sb = new StringBuilder();
        int total = grouped.values().stream().flatMap(m -> m.values().stream()).mapToInt(List::size).sum();
        sb.append(Messages.get(Messages.Keys.ADDONS_COMMANDS_HEADER, total));

        grouped.forEach((client, pluginMap) -> {
            sb.append("\n").append(client).append(":");
            pluginMap.forEach((plugin, commands) -> {
                sb.append("\n  - ").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_ADDON, plugin));
                sb.append("\n     - ").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_COMMANDS));
                commands.forEach(cmd -> sb.append("\n        ").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_ENTRY, cmd)));
            });
        });

        return sb.toString();
    }

    public static String uploadCommandsToEditor(Path dataDirectory) throws IOException {
        Path commandsPath = dataDirectory.resolve("commands.yml");
        if (!Files.exists(commandsPath)) throw new IOException("commands.yml not found!");

        Map<?, ?> yamlMap = loadYaml(commandsPath);
        byte[] gzippedJson = compressJson(new Gson().toJson(yamlMap));
        return sendToBytebin(gzippedJson);
    }

    private static Map<?, ?> loadYaml(Path commandsPath) throws IOException {
        try (InputStream in = Files.newInputStream(commandsPath)) {
            Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
            Object loaded = yaml.load(in);
            if (!(loaded instanceof Map<?, ?> map)) throw new IOException("Invalid YAML format!");
            return map;
        }
    }

    private static byte[] compressJson(String json) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(baos);
             OutputStreamWriter writer = new OutputStreamWriter(gzip, StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();
            gzip.finish();
            return baos.toByteArray();
        }
    }

    private static String sendToBytebin(byte[] gzippedJson) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(gzippedJson, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(BYTEBIN_URL + "post")
                .addHeader("Content-Encoding", "gzip")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null)
                throw new IOException("Ошибка отправки на bytebin!");
            String resp = response.body().string();
            String key = JsonParser.parseString(resp).getAsJsonObject().get("key").getAsString();
            return "https://discordbmeditor.onrender.com/#" + key;
        }
    }

    public static void applyEditsFromEditor(Path dataDirectory, String code) throws IOException {
        if (code == null || code.isEmpty()) throw new IOException("Не указан код!");

        String json = fetchJsonFromBytebin(code);
        Map<String, Object> map = new Gson().fromJson(json, Map.class);
        saveYaml(dataDirectory.resolve("commands.yml"), map);
    }

    private static String fetchJsonFromBytebin(String key) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BYTEBIN_URL + key)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null)
                throw new IOException("Ошибка получения данных из bytebin!");
            return response.body().string();
        }
    }

    private static void saveYaml(Path path, Map<String, Object> map) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setIndent(2);
        Yaml yaml = new Yaml(options);
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            yaml.dump(map, writer);
        }
    }
}
