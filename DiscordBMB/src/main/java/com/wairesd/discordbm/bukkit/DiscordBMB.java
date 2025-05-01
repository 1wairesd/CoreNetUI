package com.wairesd.discordbm.bukkit;

import com.google.gson.Gson;
import com.wairesd.discordbm.bukkit.api.DiscordBMBApi;
import com.wairesd.discordbm.bukkit.commands.CommandAdmin;
import com.wairesd.discordbm.bukkit.config.ConfigManager;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import com.wairesd.discordbm.bukkit.handler.DiscordCommandHandler;
import com.wairesd.discordbm.bukkit.models.command.Command;
import com.wairesd.discordbm.bukkit.network.NettyService;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DiscordBMB extends JavaPlugin {
    private static DiscordBMBApi api;
    private ConfigManager configManager;
    private final NettyService nettyService;
    private final Map<String, DiscordCommandHandler> commandHandlers = new HashMap<>();
    private final List<Command> addonCommands = new ArrayList<>();
    private String serverName;
    private final Gson gson = new Gson();

    private boolean invalidSecret = false;

    public DiscordBMB() {
        this.nettyService = new NettyService(this);
    }

    @Override
    public void onEnable() {
        api = new DiscordBMBApi(this);
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        serverName = Settings.getServerName();
        String host    = Settings.getVelocityHost();
        int port       = Settings.getVelocityPort();
        getCommand("discordBMB").setExecutor(new CommandAdmin(this));
        getCommand("discordBMB").setTabCompleter(new CommandAdmin(this));
        getServer().getScheduler().runTaskAsynchronously(this,
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
                                       Command addonCommand) {
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

    public void sendNettyMessage(String message) {
        nettyService.sendNettyMessage(message);
    }

    public void sendAllAddonCommands() {
        nettyService.sendAllAddonCommands(addonCommands, serverName);
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

    public interface DiscordCommandRegistrationListener {
        void onNettyConnected();
    }
}
