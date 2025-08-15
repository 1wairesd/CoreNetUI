package com.wairesd.discordbm.client.common.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.client.common.DiscordBMAPIImpl;
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
    private static final String ERROR_HANDLER_NOT_FOUND = "{\"error\":\"Command handler not found\"}";
    private static final String ERROR_INTERNAL_SERVER = "{\"error\":\"Internal server error\"}";
    private static final String CONNECTION_RESET_MESSAGE = "Connection reset";

    private static final String TYPE_AUTH_OK = "auth_ok";
    private static final String TYPE_ROLE_ACTION_RESPONSE = "role_action_response";
    private static final String TYPE_MODAL_SUBMIT = "modal_submit";

    private static final String ERROR_INVALID_SECRET = "Error: Invalid secret code";
    private static final String ERROR_NO_SECRET = "Error: No secret code provided";
    private static final String ERROR_AUTH_TIMEOUT = "Error: Authentication timeout";

    private final Platform platform;
    private final Gson gson = new Gson();
    private final PluginLogger pluginLogger;

    private enum MessageType {
        REQUEST("request"),
        CAN_HANDLE_PLACEHOLDERS("can_handle_placeholders"),
        GET_PLACEHOLDERS("get_placeholders");

        private final String type;

        MessageType(String type) {
            this.type = type;
        }

        public static MessageType from(String type) {
            for (MessageType mt : values()) {
                if (mt.type.equals(type)) {
                    return mt;
                }
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
        logReceivedMessage(message);

        if (isErrorMessage(message)) {
            handleErrorMessage(message, ctx);
            return;
        }

        try {
            processJsonMessage(ctx, message);
        } catch (Exception e) {
            logMessageProcessingError(message, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (isConnectionResetException(cause)) {
            closeContext(ctx);
        } else {
            handleGeneralException(ctx, cause);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logChannelInactive();
        ctx.fireChannelInactive();
    }

    private void processJsonMessage(ChannelHandlerContext ctx, String message) {
        JsonObject json = parseJsonMessage(message);
        String typeStr = extractMessageType(json);

        if (handleSpecialMessageTypes(ctx, json, typeStr)) {
            return;
        }

        processStandardMessageTypes(ctx, json, typeStr);
    }

    private boolean handleSpecialMessageTypes(ChannelHandlerContext ctx, JsonObject json, String typeStr) {
        switch (typeStr) {
            case TYPE_AUTH_OK:
                handleAuthOkMessage();
                return true;
            case TYPE_ROLE_ACTION_RESPONSE:
                handleRoleActionResponse(json);
                return true;
            case TYPE_MODAL_SUBMIT:
                handleModalSubmit(json);
                return true;
            default:
                return false;
        }
    }

    private void processStandardMessageTypes(ChannelHandlerContext ctx, JsonObject json, String typeStr) {
        MessageType type = MessageType.from(typeStr);

        switch (type != null ? type : MessageType.REQUEST) {
            case REQUEST -> handleRequest(json);
            case CAN_HANDLE_PLACEHOLDERS -> handleCanHandlePlaceholders(ctx, json);
            case GET_PLACEHOLDERS -> handleGetPlaceholders(ctx, json);
            default -> logUnknownMessageType(typeStr);
        }
    }

    private void handleAuthOkMessage() {
        platform.onNettyConnected();
    }

    private void handleRoleActionResponse(JsonObject json) {
        RoleActionResponse resp = gson.fromJson(json, RoleActionResponse.class);

        if (platform instanceof DiscordBMAPIImpl apiImpl) {
            RoleManagerImpl roleManager = (RoleManagerImpl) apiImpl.getRoleManager();
            roleManager.handleRoleActionResponse(resp);
        }
    }

    private void handleModalSubmit(JsonObject json) {
        String command = extractStringFromJson(json, "command");
        String requestId = extractStringFromJson(json, "requestId");
        Map<String, String> modalData = extractModalData(json);

        CommandHandler handler = getCommandHandler(command);
        if (handler != null) {
            executeModalSubmitHandler(handler, command, modalData, requestId);
        }
    }

    private void handleCanHandlePlaceholders(ChannelHandlerContext ctx, JsonObject json) {
        CanHandlePlaceholdersRequest req = gson.fromJson(json, CanHandlePlaceholdersRequest.class);

        platform.runTaskAsynchronously(() -> {
            boolean canHandle = platform.checkIfCanHandle(req.player(), req.placeholders());
            CanHandleResponse resp = buildCanHandleResponse(req.requestId(), canHandle);
            sendResponse(ctx, resp);
        });
    }

    private void handleGetPlaceholders(ChannelHandlerContext ctx, JsonObject json) {
        GetPlaceholdersRequest req = gson.fromJson(json, GetPlaceholdersRequest.class);

        platform.runTaskAsynchronously(() -> {
            platform.getPlaceholderValues(req.player(), req.placeholders())
                    .thenAccept(values -> {
                        PlaceholdersResponse resp = buildPlaceholdersResponse(req.requestId(), values);
                        sendResponse(ctx, resp);
                    });
        });
    }

    private void handleRequest(JsonObject json) {
        String command = extractStringFromJson(json, "command");
        String requestId = extractStringFromJson(json, "requestId");
        Map<String, String> options = extractOptionsFromJson(json);

        CommandHandler handler = platform.getCommandHandlers().get(command);

        if (handler != null) {
            executeCommandHandler(handler, command, options, requestId);
        } else {
            sendErrorResponse(requestId, ERROR_HANDLER_NOT_FOUND);
        }
    }

    private void handleErrorMessage(String message, ChannelHandlerContext ctx) {
        logErrorMessage(message);

        if (shouldCloseConnectionForError(message)) {
            closeContext(ctx);
        }
    }

    private boolean shouldCloseConnectionForError(String message) {
        return ERROR_INVALID_SECRET.equals(message)
                || ERROR_NO_SECRET.equals(message)
                || ERROR_AUTH_TIMEOUT.equals(message);
    }

    private CommandHandler getCommandHandler(String command) {
        var commandRegistration = platform.getCommandRegistration();

        if (commandRegistration instanceof com.wairesd.discordbm.client.common.command.CommandRegistrationImpl cmdReg) {
            return cmdReg.getCommandHandler(command);
        }

        return null;
    }

    private void executeModalSubmitHandler(CommandHandler handler, String command,
                                           Map<String, String> modalData, String requestId) {
        try {
            handler.handleFormSubmit(command, modalData, requestId);
        } catch (Exception e) {
            logHandlerException(e);
        }
    }

    private void executeCommandHandler(CommandHandler handler, String command,
                                       Map<String, String> options, String requestId) {
        platform.runTaskAsynchronously(() -> {
            try {
                handler.handleCommand(command, options, requestId);
            } catch (Exception e) {
                sendErrorResponse(requestId, ERROR_INTERNAL_SERVER);
            }
        });
    }

    private Map<String, String> extractModalData(JsonObject json) {
        Map<String, String> modalData = new HashMap<>();

        if (json.has("modalData")) {
            JsonObject formJson = json.getAsJsonObject("modalData");
            populateMapFromJsonObject(modalData, formJson);
        }

        return modalData;
    }

    private Map<String, String> extractOptionsFromJson(JsonObject json) {
        Map<String, String> options = new HashMap<>();

        if (json.has("options")) {
            JsonObject optionsJson = json.getAsJsonObject("options");
            populateMapFromJsonObject(options, optionsJson);
        }

        return options;
    }

    private void populateMapFromJsonObject(Map<String, String> map, JsonObject jsonObject) {
        for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
    }

    private CanHandleResponse buildCanHandleResponse(String requestId, boolean canHandle) {
        return new CanHandleResponse.Builder()
                .type("can_handle_response")
                .requestId(requestId)
                .canHandle(canHandle)
                .build();
    }

    private PlaceholdersResponse buildPlaceholdersResponse(String requestId, Map<String, String> values) {
        return new PlaceholdersResponse.Builder()
                .type("placeholders_response")
                .requestId(requestId)
                .values(values)
                .build();
    }

    private JsonObject parseJsonMessage(String message) {
        return gson.fromJson(message, JsonObject.class);
    }

    private String extractMessageType(JsonObject json) {
        return json.get("type").getAsString();
    }

    private String extractStringFromJson(JsonObject json, String key) {
        return json.get(key).getAsString();
    }

    private boolean isErrorMessage(String message) {
        return message.startsWith("Error:");
    }

    private boolean isConnectionResetException(Throwable cause) {
        return cause instanceof SocketException && CONNECTION_RESET_MESSAGE.equals(cause.getMessage());
    }

    private void sendResponse(ChannelHandlerContext ctx, Object response) {
        ctx.channel().writeAndFlush(gson.toJson(response));
    }

    private void sendErrorResponse(String requestId, String errorMessage) {
        platform.getNettyService().sendResponse(requestId, errorMessage);
    }

    private void closeContext(ChannelHandlerContext ctx) {
        ctx.close();
    }

    private void handleGeneralException(ChannelHandlerContext ctx, Throwable cause) {
        if (platform.isDebugErrors()) {
            String errorMessage = cause != null ? cause.getMessage() : "Unknown error";
            pluginLogger.error("Connection error: " + errorMessage, cause);
        }
        closeContext(ctx);
    }

    // Logging methods
    private void logReceivedMessage(String message) {
        if (platform.isDebugClientResponses()) {
            pluginLogger.info("Received message: " + message);
        }
    }

    private void logErrorMessage(String message) {
        if (platform.isDebugErrors()) {
            pluginLogger.warn("Received error from server: " + message);
        }
    }

    private void logMessageProcessingError(String message, Exception e) {
        pluginLogger.error("Error processing message: " + message, e);
    }

    private void logUnknownMessageType(String typeStr) {
        pluginLogger.warn("Unknown message type: " + typeStr);
    }

    private void logHandlerException(Exception e) {
        e.printStackTrace();
    }

    private void logChannelInactive() {
        pluginLogger.warn("Netty connection to host lost (channel inactive). " +
                "Attempting reconnect or manual intervention may be required.");
    }
}