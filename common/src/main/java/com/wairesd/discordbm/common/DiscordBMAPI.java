package com.wairesd.discordbm.common;

import com.google.gson.Gson;
import com.wairesd.discordbm.common.commandbuilder.CommandRegister;
import com.wairesd.discordbm.common.commandbuilder.CommandUnregister;
import com.wairesd.discordbm.common.handler.DiscordCommandHandler;
import com.wairesd.discordbm.common.listener.DiscordBMCRLB;
import com.wairesd.discordbm.common.models.command.Command;
import com.wairesd.discordbm.common.network.NettyService;
import com.wairesd.discordbm.common.platform.Platform;
import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.buttons.ButtonStyle;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class DiscordBMAPI {
    private final Platform platform;
    private final CommandRegister commandRegister;
    private final CommandUnregister commandUnregister;
    private final Gson gson = new Gson();
    private final List<Command> addonCommands = new ArrayList<>();
    private final PluginLogger pluginLogger;

    public DiscordBMAPI(Platform platform, PluginLogger pluginLogger) {
        this.platform = platform;
        this.pluginLogger = pluginLogger;
        this.commandRegister = new CommandRegister(platform, pluginLogger);
        this.commandUnregister = new CommandUnregister(platform, pluginLogger);
    }

    public void registerCommand(Command command, DiscordCommandHandler handler) {
        platform.registerCommandHandler(command.getName(), handler, null, command);
        synchronized (addonCommands) {
            addonCommands.add(command);
        }
        if (platform.getNettyService().getNettyClient() != null && platform.getNettyService().getNettyClient().isActive()) {
            commandRegister.register(command);
        }
    }

    public void registerCommand(Command command, DiscordCommandHandler handler, DiscordBMCRLB listener) {
        platform.registerCommandHandler(command.getName(), handler, listener, command);
        if (platform.getNettyService().getNettyClient() != null && platform.getNettyService().getNettyClient().isActive()) {
            commandRegister.register(command);
        }
    }

    public void sendResponseWithButtons(String requestId, EmbedDefinition embed, List<ButtonDefinition> buttons) {
        ResponseMessage respMsg = new ResponseMessage.Builder()
                .type("response")
                .requestId(requestId)
                .response(null)
                .embed(embed)
                .buttons(buttons)
                .build();
        String json = gson.toJson(respMsg);
        platform.getNettyService().sendNettyMessage(json);
    }

    public ButtonDefinition createButton(String label, String customId, ButtonStyle style) {
        return new ButtonDefinition.Builder()
                .label(label)
                .customId(customId)
                .style(style)
                .url(null)
                .disabled(false)
                .build();
    }

    public void unregisterCommand(String commandName, String pluginName) {
        if (platform.getNettyService().getNettyClient() != null && platform.getNettyService().getNettyClient().isActive()) {
            commandUnregister.unregister(commandName, pluginName);
        }

        synchronized (addonCommands) {
            addonCommands.removeIf(cmd -> cmd.getName().equals(commandName));
        }
    }

    public void sendResponse(String requestId, EmbedDefinition embed) {
        String embedJson = new Gson().toJson(embed);
        platform.getNettyService().sendResponse(requestId, embedJson);
    }

    public void sendNettyMessage(String message) {
        platform.getNettyService().sendNettyMessage(message);
    }

    public List<Command> getAddonCommands() {
        return new ArrayList<>(addonCommands);
    }

    public NettyService getNettyService() {
        return platform.getNettyService();
    }

    public String getServerName() {
        return platform.getServerName();
    }

    public PluginLogger getLogger() {
        return pluginLogger;
    }

    public void info(String message) {
        pluginLogger.info(message);
    }

    public void info(String message, Throwable t) {
        pluginLogger.info(message, t);
    }

    public void info(String message, Object... args) {
        pluginLogger.info(message, args);
    }

    public void warn(String message) {
        pluginLogger.warn(message);
    }

    public void warn(String message, Throwable t) {
        pluginLogger.warn(message, t);
    }

    public void warn(String message, Object... args) {
        pluginLogger.warn(message, args);
    }

    public void error(String message) {
        pluginLogger.error(message);
    }

    public void error(String message, Throwable t) {
        pluginLogger.error(message, t);
    }

    public void error(String message, Object... args) {
        pluginLogger.error(message, args);
    }

    public String parseColorString(String message) {
        return ColorUtils.parseString(message);
    }

    public Component parseColorComponent(String message) {
        return ColorUtils.parseComponent(message);
    }

    public String autoParseColor(String message, boolean isConsole) {
        return ColorUtils.autoParse(message, isConsole);
    }
}