package com.wairesd.discordbm.client.common.role;

import com.google.gson.Gson;
import com.wairesd.discordbm.api.role.RoleManager;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.models.request.AddRoleRequest;
import com.wairesd.discordbm.common.models.request.RemoveRoleRequest;
import com.wairesd.discordbm.common.models.response.RoleActionResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RoleManagerImpl implements RoleManager {
    private final Platform platform;
    private final Gson gson = new Gson();
    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    public RoleManagerImpl(Platform platform) {
        this.platform = platform;
    }

    @Override
    public CompletableFuture<Boolean> addRole(String guildId, String userId, String roleId) {
        String requestId = UUID.randomUUID().toString();
        AddRoleRequest request = new AddRoleRequest(guildId, userId, roleId, requestId);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        sendRequest(gson.toJson(request));
        return future;
    }

    @Override
    public CompletableFuture<Boolean> removeRole(String guildId, String userId, String roleId) {
        String requestId = UUID.randomUUID().toString();
        RemoveRoleRequest request = new RemoveRoleRequest(guildId, userId, roleId, requestId);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        sendRequest(gson.toJson(request));
        return future;
    }

    private void sendRequest(String json) {
        var nettyService = platform.getNettyService();
        if (nettyService != null && nettyService.getNettyClient() != null && nettyService.getNettyClient().isActive()) {
            nettyService.getNettyClient().send(json);
        }
    }

    public void handleRoleActionResponse(RoleActionResponse response) {
        CompletableFuture<Boolean> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            if (response.isSuccess()) {
                future.complete(true);
            } else {
                future.completeExceptionally(new RuntimeException(response.getError()));
            }
        }
    }
} 