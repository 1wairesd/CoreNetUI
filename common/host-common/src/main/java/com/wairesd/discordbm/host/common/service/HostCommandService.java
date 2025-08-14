package com.wairesd.discordbm.host.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandRegistration;
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
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.List;
import java.util.StringJoiner;
import okhttp3.*;

public class HostCommandService {
    private static final String BYTEBIN_URL = "https://bytebin.lucko.me/";

    public static String reload(Path dataDirectory, DiscordBMHPlatformManager platformManager) {
        WebhookScheduler.shutdown();
        ConfigManager.ConfigureReload();
        WebhookScheduler.start();
        if (platformManager != null && platformManager.getNettyServer() != null) {
            platformManager.updateActivity();
            platformManager.getCommandManager().loadAndRegisterCommands();
        }
        DBMAPI.getInstance().getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.FULL));
        DBMAPI.getInstance().getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.CONFIG));
        DBMAPI.getInstance().getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.COMMANDS));
        DBMAPI.getInstance().getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.NETWORK));
        return Messages.get(Messages.Keys.RELOAD_SUCCESS);
    }

    public static String toggleWebhook(Path dataDirectory, String webhookName, boolean enable) {
        WebhookScheduler.shutdown();
        String result = WebhookManager.handleWebhookToggle(dataDirectory, webhookName, enable);
        WebhookScheduler.start();
        return result;
    }

    public static String listClients(DiscordBMHPlatformManager platformManager) {
        NettyServer nettyServer = platformManager.getNettyServer();
        if (nettyServer == null) {
            return Messages.get(Messages.Keys.NO_ACTIVE_CLIENTS);
        }
        List<ClientInfo> clients = nettyServer.getActiveClientsInfo();
        if (clients.isEmpty()) {
            return Messages.get(Messages.Keys.NO_CONNECTED_CLIENTS);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Clients (").append(clients.size()).append("):");
        for (ClientInfo client : clients) {
            sb.append("\n- ").append(client.name)
              .append(" (").append(client.ip).append(":").append(client.port).append(") time: ")
              .append(formatUptime(client.uptimeMillis));
        }
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
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(Messages.get(Messages.Keys.HELP_HEADER, context));
        joiner.add(Messages.get(Messages.Keys.HELP_RELOAD, context));
        joiner.add(Messages.get(Messages.Keys.HELP_WEBHOOK, context));
        joiner.add(Messages.get(Messages.Keys.HELP_EDITOR, context));
        joiner.add(Messages.get(Messages.Keys.HELP_APPLYEDITS, context));
        joiner.add(Messages.get(Messages.Keys.HELP_CUSTOM_COMMANDS, context));
        joiner.add(Messages.get(Messages.Keys.HELP_ADDONS_COMMANDS, context));
        return joiner.toString();
    }

    public static String getCustomCommands(MessageContext context) {
        List<CommandStructured> customCommands = com.wairesd.discordbm.host.common.config.configurators.Commands.getCustomCommands();
        if (customCommands.isEmpty()) {
            return Messages.get(Messages.Keys.CUSTOM_COMMANDS_EMPTY, context);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(Messages.Keys.CUSTOM_COMMANDS_HEADER, customCommands.size()));
        for (var cmd : customCommands) {
            sb.append("\n").append(Messages.get(Messages.Keys.CUSTOM_COMMANDS_ENTRY, cmd.getName()));
        }
        return sb.toString();
    }

    public static String getAddonCommands(DiscordBMHPlatformManager platformManager, MessageContext context) {
        if (platformManager == null || platformManager.getNettyServer() == null) {
            return Messages.get(Messages.Keys.ADDONS_COMMANDS_EMPTY, context);
        }
        var nettyServer = platformManager.getNettyServer();
        var commandToServers = nettyServer.getCommandToServers();
        var customCommands = com.wairesd.discordbm.host.common.config.configurators.Commands.getCustomCommands();
        java.util.Set<String> customNames = new java.util.HashSet<>();
        for (var cmd : customCommands) customNames.add(cmd.getName());

        var commandDefinitions = nettyServer.getCommandDefinitions();
        Map<String, Map<String, List<String>>> grouped = new java.util.HashMap<>();
        int total = 0;
        for (var entry : commandToServers.entrySet()) {
            String command = entry.getKey();
            if (customNames.contains(command)) continue;
            String plugin;
            var def = commandDefinitions.get(command);
            if (def != null && def.pluginName() != null && !def.pluginName().isEmpty()) {
                plugin = def.pluginName();
            } else {
                plugin = nettyServer.getPluginForCommand(command);
            }
            for (var serverInfo : entry.getValue()) {
                String client = serverInfo.serverName();
                grouped.computeIfAbsent(client, k -> new java.util.HashMap<>())
                        .computeIfAbsent(plugin, k -> new java.util.ArrayList<>())
                        .add(command);
                total++;
            }
        }
        
        var api = DBMAPI.getInstance();
        if (api != null) {
            CommandRegistration reg = api.getCommandRegistration();
            if (reg instanceof HostCommandRegistration hostReg) {
                var hostCommands = hostReg.getRegisteredCommands();
                for (var cmd : hostCommands) {
                    String plugin = cmd.getPluginName() != null ? cmd.getPluginName() : "host";
                    grouped.computeIfAbsent("host", k -> new java.util.HashMap<>())
                            .computeIfAbsent(plugin, k -> new java.util.ArrayList<>())
                            .add(cmd.getName());
                    total++;
                }
            }
        }
        if (total == 0) {
            return Messages.get(Messages.Keys.ADDONS_COMMANDS_EMPTY, context);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.get(Messages.Keys.ADDONS_COMMANDS_HEADER, total));
        for (var clientEntry : grouped.entrySet()) {
            String client = clientEntry.getKey();
            sb.append("\n").append(client).append(":");
            for (var pluginEntry : clientEntry.getValue().entrySet()) {
                String plugin = pluginEntry.getKey();
                List<String> commands = pluginEntry.getValue();
                sb.append("\n  - ").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_ADDON, plugin));
                sb.append("\n     - ").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_COMMANDS));
                for (String cmd : commands) {
                    sb.append("\n        ").append(Messages.get(Messages.Keys.ADDONS_COMMANDS_ENTRY, cmd));
                }
            }
        }
        return sb.toString();
    }

    public static String uploadCommandsToEditor(Path dataDirectory) throws IOException {
        Path commandsPath = dataDirectory.resolve("commands.yml");
        if (!Files.exists(commandsPath)) {
            throw new IOException("commands.yml not found!");
        }
        try (InputStream in = Files.newInputStream(commandsPath)) {
            Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
            Object loaded = yaml.load(in);
            if (!(loaded instanceof Map<?, ?> map)) {
                throw new IOException("Invalid YAML format!");
            }
            String json = new Gson().toJson(map);
            byte[] gzipped;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 GZIPOutputStream gzip = new GZIPOutputStream(baos);
                 OutputStreamWriter writer = new OutputStreamWriter(gzip, StandardCharsets.UTF_8)) {
                writer.write(json);
                writer.flush();
                gzip.finish();
                gzipped = baos.toByteArray();
            }
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(gzipped, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                .url(BYTEBIN_URL + "post")
                .addHeader("Content-Encoding", "gzip")
                .post(body)
                .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IOException("Ошибка отправки на bytebin!");
                }
                String resp = response.body().string();
                String key = JsonParser.parseString(resp).getAsJsonObject().get("key").getAsString();
                return "https://discordbmeditor.onrender.com/#" + key;
            }
        }
    }

    public static void applyEditsFromEditor(Path dataDirectory, String code) throws IOException {
        if (code == null || code.isEmpty()) {
            throw new IOException("Не указан код!");
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BYTEBIN_URL + code).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Ошибка загрузки данных с bytebin!");
            }
            String json = response.body().string();
            Map<String, Object> map = new Gson().fromJson(json, Map.class);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);
            Path commandsPath = dataDirectory.resolve("commands.yml");
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(commandsPath), StandardCharsets.UTF_8)) {
                yaml.dump(map, writer);
            }
        }
    }
} 