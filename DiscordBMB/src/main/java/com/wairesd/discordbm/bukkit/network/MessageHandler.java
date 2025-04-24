package com.wairesd.discordbm.bukkit.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import com.wairesd.discordbm.bukkit.handler.DiscordCommandHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
public class MessageHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private final DiscordBMB plugin;
    private final Gson gson = new Gson();

    public MessageHandler(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles inbound messages from the channel and processes them based on their type.
     *
     * @param ctx     the context of the channel handler
     * @param message the received message as a String
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        if (Settings.isDebugClientResponses()) {
            logger.debug("Received message: {}", message);
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
            } else {
                logger.warn("Unknown message type: {}", type);
            }
        } catch (Exception e) {
            if (Settings.isDebugErrors()) {
                logger.error("Error processing message: {}", message, e);
            }
        }
    }

    /**
     * Handles error messages received from the server and performs appropriate actions
     * such as logging the error and closing the channel context if necessary.
     *
     * @param message the error message received from the server
     * @param ctx     the context of the channel handler
     */
    private void handleErrorMessage(String message, ChannelHandlerContext ctx) {
        if (Settings.isDebugErrors()) {
            logger.warn("Received error from server: {}", message);
        }
        switch (message) {
            case "Error: Invalid secret code":
            case "Error: No secret code provided":
                plugin.setInvalidSecret(true);
                ctx.close();
                break;
            case "Error: Authentication timeout":
                if (Settings.isDebugAuthentication()) {
                    logger.warn("Authentication timeout occurred");
                }
                ctx.close();
                break;
            default:
                break;
        }
    }

    /**
     * Handles a request by extracting its command, options, and request ID from the provided JSON object.
     * Executes the corresponding command handler if available or sends a "Command not found" response.
     *
     * @param json the input JSON object containing the request details, including the command, request ID,
     *             and optionally additional options
     */
    private void handleRequest(JsonObject json) {
        String command = json.get("command").getAsString();
        String requestId = json.get("requestId").getAsString();

        Map<String, String> options = new HashMap<>();
        if (json.has("options")) {
            JsonObject optionsJson = json.get("options").getAsJsonObject();
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

    /**
     * Handles exceptions caught during the operation of the channel.
     * Logs the error message if debug error logging is enabled in the settings
     * and ensures that the channel context is closed to prevent further issues.
     *
     * @param ctx   the context of the channel handler where the exception occurred
     * @param cause the throwable that was caught
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (Settings.isDebugErrors()) {
            logger.error("Connection error: {}", cause.getMessage(), cause);
        }
        ctx.close();
    }
}
