package com.wairesd.discordbm.bukkit.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import com.wairesd.discordbm.bukkit.handler.DiscordCommandHandler;
import com.wairesd.discordbm.common.models.placeholders.request.CanHandlePlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.request.GetPlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.response.CanHandleResponse;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;
import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

/**
 * Handles communication between the Netty client and the plugin backend by processing
 * incoming messages and sending appropriate responses.
 *
 * This class extends {@code SimpleChannelInboundHandler<String>} to handle string-based
 * messages received over a Netty channel. Incoming messages are parsed, and actions
 * such as error handling, command execution, and placeholder resolution are performed.
 */
public class MessageHandler extends SimpleChannelInboundHandler<String> {
    private final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());

    private final DiscordBMB plugin;
    private final Gson gson = new Gson();

    public MessageHandler(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        if (Settings.isDebugClientResponses()) {
            pluginLogger.info("Received message: {}", message);
        }

        if (message.startsWith("Error:")) {
            handleErrorMessage(message, ctx);
            return;
        }

        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();

            if ("request".equals(type)) {
                handleRequest(json);
            } else if ("can_handle_placeholders".equals(type)) {
                CanHandlePlaceholdersRequest req = gson.fromJson(json, CanHandlePlaceholdersRequest.class);
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    boolean canHandle = plugin.checkIfCanHandle(req.player(), req.placeholders());
                    CanHandleResponse resp = new CanHandleResponse(
                            "can_handle_response",
                            req.requestId(),
                            canHandle
                    );
                    ctx.channel().writeAndFlush(gson.toJson(resp));
                });
            } else if ("get_placeholders".equals(type)) {
                GetPlaceholdersRequest req = gson.fromJson(json, GetPlaceholdersRequest.class);
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    Map<String, String> values = plugin.getPlaceholderValues(req.player(), req.placeholders());
                    if (Settings.isDebugResolvePlaceholders()) {
                        pluginLogger.info("Resolving placeholders for player {}: {}", req.player(), values);
                    }
                    PlaceholdersResponse resp = new PlaceholdersResponse(
                            "placeholders_response",
                            req.requestId(),
                            values
                    );
                    ctx.channel().writeAndFlush(gson.toJson(resp));
                });
            } else {
                pluginLogger.warn("Unknown message type: {}", type);
            }
        } catch (Exception e) {
            if (Settings.isDebugErrors()) {
                pluginLogger.error("Error processing message: {}", message, e);
            }
        }
    }

    private void handleErrorMessage(String message, ChannelHandlerContext ctx) {
        if (Settings.isDebugErrors()) {
            pluginLogger.warn("Received error from server: {}", message);
        }
        switch (message) {
            case "Error: Invalid secret code":
            case "Error: No secret code provided":
                plugin.setInvalidSecret(true);
                ctx.close();
                break;
            case "Error: Authentication timeout":
                if (Settings.isDebugAuthentication()) {
                    pluginLogger.warn("Authentication timeout occurred");
                }
                ctx.close();
                break;
            default:
                break;
        }
    }

    private void handleRequest(JsonObject json) {
        String command = json.get("command").getAsString();
        String requestId = json.get("requestId").getAsString();

        Map<String, String> options = new HashMap<>();
        if (json.has("options")) {
            JsonObject optionsJson = json.getAsJsonObject("options");
            for (Map.Entry<String, com.google.gson.JsonElement> entry : optionsJson.entrySet()) {
                options.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        DiscordCommandHandler handler = plugin.getCommandHandlers().get(command);
        if (handler != null) {
            String[] args = options.values().toArray(new String[0]);
            handler.handleCommand(command, args, requestId);
        } else {
            plugin.sendResponse(requestId, "Command not found.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (Settings.isDebugErrors()) {
            pluginLogger.error("Connection error: {}", cause.getMessage(), cause);
        }
        ctx.close();
    }
}