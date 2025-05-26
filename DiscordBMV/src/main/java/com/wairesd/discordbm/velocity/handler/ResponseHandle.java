package com.wairesd.discordbm.velocity.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.velocity.discord.response.ResponseHandler;

public class ResponseHandle {
    private final Gson gson = new Gson();
    private boolean authenticated = false;

    public void handleResponse(JsonObject json) {
        if (!authenticated) return;
        ResponseMessage respMsg = gson.fromJson(json, ResponseMessage.class);
        ResponseHandler.handleResponse(respMsg);
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
