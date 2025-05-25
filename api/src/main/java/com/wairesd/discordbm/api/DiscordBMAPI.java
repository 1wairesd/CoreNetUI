package com.wairesd.discordbm.api;

import com.google.gson.Gson;
import com.wairesd.discordbm.api.commandbuilder.CommandRegister;
import com.wairesd.discordbm.api.commandbuilder.CommandUnregister;
import com.wairesd.discordbm.api.handle.DiscordCommandHandler;
import com.wairesd.discordbm.api.listener.DiscordCommandRegistrationListener;
import com.wairesd.discordbm.api.models.command.Command;
import com.wairesd.discordbm.api.network.NettyService;
import com.wairesd.discordbm.api.platform.Platform;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;

public class DiscordBMAPI {
    private final Platform platform;
    private final CommandRegister commandRegister;
    private final CommandUnregister commandUnregister;

    public DiscordBMAPI(Platform platform, PluginLogger pluginLogger) {
        this.platform = platform;
        this.commandRegister = new CommandRegister(platform, pluginLogger);
        this.commandUnregister = new CommandUnregister(platform, pluginLogger);
    }

    public void registerCommand(Command command, DiscordCommandHandler handler, DiscordCommandRegistrationListener listener) {
        platform.registerCommandHandler(command.name, handler, listener, command);
        if (platform.getNettyService().getNettyClient() != null && platform.getNettyService().getNettyClient().isActive()) {
            commandRegister.register(command);
        }
    }

    public void unregisterCommand(String commandName, String pluginName) {
        if (platform.getNettyService().getNettyClient() != null && platform.getNettyService().getNettyClient().isActive()) {
            commandUnregister.unregister(commandName, pluginName);
        }
    }

    public void sendResponse(String requestId, EmbedDefinition embed) {
        String embedJson = new Gson().toJson(embed);
        platform.getNettyService().sendResponse(requestId, embedJson);
    }

    public void sendNettyMessage(String message) {
        platform.getNettyService().sendNettyMessage(message);
    }

    public NettyService getNettyService() {
        return platform.getNettyService();
    }

}