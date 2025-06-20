package com.wairesd.discordbm.common.platform;

import com.wairesd.discordbm.common.listener.DiscordBMCRLB;
import com.wairesd.discordbm.common.handler.DiscordCommandHandler;
import com.wairesd.discordbm.common.models.command.Command;
import com.wairesd.discordbm.common.network.NettyService;

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
    void onNettyConnected();
}