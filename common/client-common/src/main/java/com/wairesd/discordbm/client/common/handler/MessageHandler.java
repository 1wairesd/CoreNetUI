package com.wairesd.discordbm.client.common.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.models.placeholders.request.CanHandlePlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.request.GetPlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.response.CanHandleResponse;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;
import com.wairesd.discordbm.common.models.response.RoleActionResponse;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.client.common.role.RoleManagerImpl;
import com.wairesd.discordbm.api.command.CommandHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class MessageHandler extends SimpleChannelInboundHandler<String> {
    private final Platform platform;
    private final Gson gson = new Gson();
    private final PluginLogger pluginLogger;
    private static final String ERROR_HANDLER_NOT_FOUND = "{\"error\":\"Command handler not found\"}";
    private enum MessageType {
        REQUEST("request"),
        CAN_HANDLE_PLACEHOLDERS("can_handle_placeholders"),
        GET_PLACEHOLDERS("get_placeholders");
        private final String type;
        MessageType(String type) { this.type = type; }
        public static MessageType from(String type) {
            for (MessageType mt : values()) {
                if (mt.type.equals(type)) return mt;
            }
            return null;
        }
    }

    public MessageHandler(Platform platform, PluginLogger pluginLogger) {
        this.platform = platform;
        this.pluginLogger = pluginLogger;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        if (platform.isDebugClientResponses()) {
            pluginLogger.info("Received message: " + message);
        }
        if (message.startsWith("Error:")) {
            handleErrorMessage(message, ctx);
            return;
        }
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String typeStr = json.get("type").getAsString();
            if ("role_action_response".equals(typeStr)) {
                RoleActionResponse resp = gson.fromJson(json, RoleActionResponse.class);
                if (platform instanceof com.wairesd.discordbm.client.common.DiscordBMBAPIImpl apiImpl) {
                    ((RoleManagerImpl) apiImpl.getRoleManager()).handleRoleActionResponse(resp);
                }
                return;
            }
            if ("form_submit".equals(typeStr)) {
                String command = json.get("command").getAsString();
                String requestId = json.get("requestId").getAsString();
                Map<String, String> formData = new HashMap<>();
                if (json.has("formData")) {
                    JsonObject formJson = json.getAsJsonObject("formData");
                    for (Map.Entry<String, com.google.gson.JsonElement> entry : formJson.entrySet()) {
                        formData.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }

                CommandHandler handler = null;
                
                var commandRegistration = platform.getCommandRegistration();
                
                if (commandRegistration instanceof com.wairesd.discordbm.client.common.command.CommandRegistrationImpl cmdReg) {
                    handler = cmdReg.getCommandHandler(command);
                }
                if (handler != null) {
                    try {
                        handler.handleFormSubmit(command, formData, requestId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            MessageType type = MessageType.from(typeStr);
            switch (type != null ? type : MessageType.REQUEST) {
                case REQUEST -> handleRequest(json);
                case CAN_HANDLE_PLACEHOLDERS -> {
                    CanHandlePlaceholdersRequest req = gson.fromJson(json, CanHandlePlaceholdersRequest.class);
                    platform.runTaskAsynchronously(() -> {
                        boolean canHandle = platform.checkIfCanHandle(req.player(), req.placeholders());
                        CanHandleResponse resp = new CanHandleResponse.Builder()
                                .type("can_handle_response")
                                .requestId(req.requestId())
                                .canHandle(canHandle)
                                .build();
                        ctx.channel().writeAndFlush(gson.toJson(resp));
                    });
                }
                case GET_PLACEHOLDERS -> {
                    GetPlaceholdersRequest req = gson.fromJson(json, GetPlaceholdersRequest.class);
                    platform.runTaskAsynchronously(() -> {
                        platform.getPlaceholderValues(req.player(), req.placeholders())
                            .thenAccept(values -> {
                                PlaceholdersResponse resp = new PlaceholdersResponse.Builder()
                                        .type("placeholders_response")
                                        .requestId(req.requestId())
                                        .values(values)
                                        .build();
                                ctx.channel().writeAndFlush(gson.toJson(resp));
                            });
                    });
                }
                default -> pluginLogger.warn("Unknown message type: " + typeStr);
            }
        } catch (Exception e) {
            pluginLogger.error("Error processing message: " + message, e);
        }
    }

    private void handleErrorMessage(String message, ChannelHandlerContext ctx) {
        if (platform.isDebugErrors()) {
            pluginLogger.warn("Received error from server: " + message);
        }
        switch (message) {
            case "Error: Invalid secret code":
            case "Error: No secret code provided":
            case "Error: Authentication timeout":
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
        CommandHandler handler = platform.getCommandHandlers().get(command);
        if (handler != null) {
            platform.runTaskAsynchronously(() -> {
                try {
                    handler.handleCommand(command, options, requestId);
                } catch (Exception e) {
                    platform.getNettyService().sendResponse(requestId,
                            "{\"error\":\"Internal server error\"}");
                }
            });
        } else {
            platform.getNettyService().sendResponse(requestId, ERROR_HANDLER_NOT_FOUND);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException && "Connection reset".equals(cause.getMessage())) {
            ctx.close();
        } else if (platform.isDebugErrors()) {
            pluginLogger.error("Connection error: " + (cause != null ? cause.getMessage() : "Unknown error"), cause);
            ctx.close();
        } else {
            ctx.close();
        }
    }
}