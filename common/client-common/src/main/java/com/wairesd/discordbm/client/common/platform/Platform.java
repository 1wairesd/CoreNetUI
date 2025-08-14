package com.wairesd.discordbm.client.common.platform;

import com.wairesd.discordbm.client.common.listener.DiscordBMCRLB;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.client.common.models.command.Command;
import com.wairesd.discordbm.client.common.network.NettyService;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;

public interface Platform {
    String getHostIp();
    int getHostPort();
    String getServerName();
    String getSecretCode();
    boolean isDebugCommandRegistrations();
    boolean isDebugClientResponses();
    boolean isDebugConnections();
    boolean isDebugErrors();
    NettyService getNettyService();
    void registerCommandHandler(String command, CommandHandler handler, DiscordBMCRLB listener, Command addonCommand);
    Map<String, CommandHandler> getCommandHandlers();
    CommandRegistration getCommandRegistration();
    boolean checkIfCanHandle(String playerName, List<String> placeholders);
    CompletableFuture<Map<String, String>> getPlaceholderValues(String playerName, List<String> placeholders);
    void runTaskAsynchronously(Runnable task);
    void runTaskLaterAsynchronously(Runnable task, long delay);
    void onNettyConnected();
    boolean isConnected();
}