package com.wairesd.discordbm.client.common.platform;

import com.wairesd.discordbm.client.common.listener.DiscordBMCRLB;
import com.wairesd.discordbm.client.common.handler.DiscordCommandHandler;
import com.wairesd.discordbm.client.common.models.command.Command;
import com.wairesd.discordbm.client.common.network.NettyService;

import java.util.List;
import java.util.Map;

public interface Platform {
    String getVelocityHost();
    int getVelocityPort();
    String getServerName();
    String getSecretCode();
    boolean isDebugCommandRegistrations();
    boolean isDebugClientResponses();
    boolean isDebugConnections();
    boolean isDebugErrors();
    NettyService getNettyService();
    void registerCommandHandler(String command, DiscordCommandHandler handler, DiscordBMCRLB listener, Command addonCommand);
    Map<String, DiscordCommandHandler> getCommandHandlers();
    boolean checkIfCanHandle(String playerName, List<String> placeholders);
    Map<String, String> getPlaceholderValues(String playerName, List<String> placeholders);
    void runTaskAsynchronously(Runnable task);
    void runTaskLaterAsynchronously(Runnable task, long delay);
    void onNettyConnected();
}