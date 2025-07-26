package com.wairesd.discordbm.client.common.platform;

import com.wairesd.discordbm.client.common.config.configurators.Settings;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.client.common.command.CommandRegistrationImpl;
import com.wairesd.discordbm.client.common.listener.DiscordBMCRLB;
import com.wairesd.discordbm.client.common.models.command.Command;
import com.wairesd.discordbm.client.common.network.NettyService;
import com.wairesd.discordbm.client.common.placeholders.PlaceholderService;
import com.wairesd.discordbm.common.logging.LoggerAdapter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPlatform implements Platform {
    protected final PluginLogger pluginLogger;
    protected final NettyService nettyService;
    protected final Map<String, CommandHandler> commandHandlers = new HashMap<>();
    protected final PlaceholderService placeholderService;
    protected final Set<DiscordBMCRLB> listeners = new HashSet<>();
    protected final List<Command> addonCommands = new ArrayList<>();
    protected final CommandRegistrationImpl commandRegistration;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AbstractPlatform(PluginLogger pluginLogger, PlatformPlaceholder platformPlaceholderService) {
        this.pluginLogger = pluginLogger;
        this.nettyService = new NettyService(() -> this, pluginLogger);
        this.placeholderService = new PlaceholderService(platformPlaceholderService);
        this.commandRegistration = new CommandRegistrationImpl(this, new LoggerAdapter(pluginLogger));
    }

    @Override
    public String getVelocityHost() {
        return Settings.getVelocityHost();
    }

    @Override
    public int getVelocityPort() {
        return Settings.getVelocityPort();
    }

    @Override
    public String getServerName() {
        return Settings.getServerName();
    }

    @Override
    public String getSecretCode() {
        return Settings.getSecretCode();
    }

    @Override
    public boolean isDebugCommandRegistrations() {
        return Settings.isDebugCommandRegistrations();
    }

    @Override
    public boolean isDebugClientResponses() {
        return Settings.isDebugClientResponses();
    }

    @Override
    public boolean isDebugConnections() {
        return Settings.isDebugConnections();
    }

    @Override
    public boolean isDebugErrors() {
        return Settings.isDebugErrors();
    }

    @Override
    public NettyService getNettyService() {
        return nettyService;
    }

    @Override
    public void registerCommandHandler(String command, CommandHandler handler, DiscordBMCRLB listener, Command addonCommand) {
        commandHandlers.put(command, handler);
        if (addonCommand != null) {
            synchronized (addonCommands) {
                addonCommands.add(addonCommand);
                if (Settings.isDebugCommandRegistrations()) {
                    pluginLogger.info("Addon team registered: " + addonCommand.getName());
                }
            }
        }
        if (listener != null && listeners.add(listener)) {
            if (nettyService.getNettyClient() != null && nettyService.getNettyClient().isActive()) {
                listener.onNettyConnected();
            }
        }
    }

    @Override
    public void onNettyConnected() {
        for (DiscordBMCRLB listener : listeners) {
            listener.onNettyConnected();
        }
        List<Command> commands = getAddonCommands();
        if (!commands.isEmpty() && nettyService.getNettyClient() != null) {
            nettyService.registerCommands(commands);
        }
    }

    @Override
    public Map<String, CommandHandler> getCommandHandlers() {
        return Collections.unmodifiableMap(commandHandlers);
    }

    @Override
    public boolean checkIfCanHandle(String playerName, List<String> placeholders) {
        return placeholderService.checkIfCanHandle(playerName, placeholders);
    }

    @Override
    public CompletableFuture<Map<String, String>> getPlaceholderValues(String playerName, List<String> placeholders) {
        return CompletableFuture.supplyAsync(() -> placeholderService.getPlaceholderValues(playerName, placeholders));
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable task, long delay) {
        long delayMs = (delay * 1000) / 20;
        scheduler.schedule(task, delayMs, TimeUnit.MILLISECONDS);
    }

    public List<Command> getAddonCommands() {
        synchronized (addonCommands) {
            return new ArrayList<>(addonCommands);
        }
    }

    @Override
    public CommandRegistration getCommandRegistration() {
        return commandRegistration;
    }

    @Override
    public boolean isConnected() {
        return nettyService != null && nettyService.getNettyClient() != null && nettyService.getNettyClient().isActive();
    }
} 