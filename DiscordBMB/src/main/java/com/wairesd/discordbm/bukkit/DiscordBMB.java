package com.wairesd.discordbm.bukkit;

import com.google.gson.Gson;
import com.wairesd.discordbm.bukkit.api.DiscordBMBApi;
import com.wairesd.discordbm.bukkit.commands.CommandAdmin;
import com.wairesd.discordbm.bukkit.config.ConfigManager;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import com.wairesd.discordbm.bukkit.handler.DiscordCommandHandler;
import com.wairesd.discordbm.bukkit.network.NettyService;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DiscordBMB extends JavaPlugin {
    private static DiscordBMBApi api;
    private ConfigManager configManager;
    private final NettyService nettyService;
    private final Map<String, DiscordCommandHandler> commandHandlers = new HashMap<>();
    private final List<com.wairesd.discordbm.bukkit.models.command.Command> addonCommands = new ArrayList<>();
    private String serverName;
    private final Gson gson = new Gson();

    private boolean invalidSecret = false;

    private static final ConcurrentHashMap<String, CachedValue> placeholderCache = new ConcurrentHashMap<>();

    private static class CachedValue {
        String value;
        long timestamp;

        public CachedValue(String value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public DiscordBMB() {
        this.nettyService = new NettyService(this);
    }

    @Override
    public void onEnable() {
        api = new DiscordBMBApi(this);
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        serverName = Settings.getServerName();
        String host = Settings.getVelocityHost();
        int port = Settings.getVelocityPort();

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> {
                    long now = System.currentTimeMillis();
                    placeholderCache.entrySet().removeIf(entry ->
                            now - entry.getValue().timestamp > 5000
                    );
                },
                1200L, 1200L
        );

        getCommand("discordBMB").setExecutor(new CommandAdmin(this));
        getCommand("discordBMB").setTabCompleter(new CommandAdmin(this));
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this,
                () -> nettyService.initializeNettyClient(host, port)
        );
    }

    @Override
    public void onDisable() {
        nettyService.closeNettyConnection();
    }

    public void registerCommandHandler(String command,
                                       DiscordCommandHandler handler,
                                       DiscordCommandRegistrationListener listener,
                                       com.wairesd.discordbm.bukkit.models.command.Command addonCommand) {
        commandHandlers.put(command, handler);
        if (addonCommand != null) {
            synchronized (addonCommands) {
                addonCommands.add(addonCommand);
                if (Settings.isDebugCommandRegistrations()) {
                    getLogger().info("Registered addon command: " + addonCommand.name);
                }
            }
        }
        if (listener != null
                && nettyService.getNettyClient() != null
                && nettyService.getNettyClient().isActive()) {
            listener.onNettyConnected();
        }
    }

    public void sendResponse(String requestId, String embedJson) {
        nettyService.sendResponse(requestId, embedJson);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public NettyService getNettyService() {
        return nettyService;
    }

    public String getServerName() {
        return serverName;
    }

    public static DiscordBMBApi getApi() {
        return api;
    }

    public void setInvalidSecret(boolean invalid) {
        this.invalidSecret = invalid;
    }

    public boolean isInvalidSecret() {
        return invalidSecret;
    }

    public Map<String, DiscordCommandHandler> getCommandHandlers() {
        return Collections.unmodifiableMap(commandHandlers);
    }

    public boolean checkIfCanHandle(String playerName, List<String> placeholders) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerName));
        boolean canHandle = false;
        for (String placeholder : placeholders) {
            String key = player.getUniqueId() + ":" + placeholder;
            CachedValue cached = placeholderCache.get(key);
            String result;
            if (cached != null && System.currentTimeMillis() - cached.timestamp < 3000) {
                result = cached.value;
            } else {
                result = PlaceholderAPI.setPlaceholders(player, placeholder);
                placeholderCache.put(key, new CachedValue(result));
            }
            if (!result.equals(placeholder)) {
                canHandle = true;
            }
        }
        return canHandle;
    }

    public Map<String, String> getPlaceholderValues(String playerName, List<String> placeholders) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerName));
        Future<Map<String, String>> future = Bukkit.getScheduler().callSyncMethod(this, () -> {
            Map<String, String> values = new HashMap<>();
            for (String placeholder : placeholders) {
                String key = player.getUniqueId() + ":" + placeholder;
                CachedValue cached = placeholderCache.get(key);
                if (cached != null && System.currentTimeMillis() - cached.timestamp < 3000) {
                    values.put(placeholder, cached.value);
                } else {
                    String result = PlaceholderAPI.setPlaceholders(player, placeholder);
                    values.put(placeholder, result);
                    placeholderCache.put(key, new CachedValue(result));
                }
            }
            return values;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public interface DiscordCommandRegistrationListener {
        void onNettyConnected();
    }
}