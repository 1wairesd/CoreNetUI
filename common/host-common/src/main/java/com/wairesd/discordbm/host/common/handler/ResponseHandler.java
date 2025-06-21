package com.wairesd.discordbm.host.common.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final Gson gson = new Gson();
    private boolean authenticated = false;

    public void handleResponse(JsonObject json) {
        String requestId = json.get("requestId").getAsString();
        logger.info("Processing response with requestId: {}", requestId);
        ResponseMessage respMsg = gson.fromJson(json, ResponseMessage.class);
        com.wairesd.discordbm.host.common.discord.response.ResponseHandler.handleResponse(respMsg);
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}