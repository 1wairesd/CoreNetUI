package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.placeholders;

import com.google.gson.Gson;
import com.wairesd.discordbm.common.models.placeholders.request.CanHandlePlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.request.GetPlaceholdersRequest;
import com.wairesd.discordbm.common.models.placeholders.response.PlaceholdersResponse;
import com.wairesd.discordbm.velocity.network.NettyServer;
import io.netty.channel.Channel;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;

public class PlaceholderRequestSender {
    private final NettyServer nettyServer;
    private final Logger logger;
    private final Gson gson = new Gson();

    public PlaceholderRequestSender(NettyServer nettyServer, Logger logger) {
        this.nettyServer = nettyServer;
        this.logger = logger;
    }

    public CompletableFuture<Boolean> sendCanHandlePlaceholdersRequest(Channel channel, String playerName, List<String> placeholders) {
        String requestId = UUID.randomUUID().toString();
        CanHandlePlaceholdersRequest req = new CanHandlePlaceholdersRequest("can_handle_placeholders", playerName, placeholders, requestId);
        String json = gson.toJson(req);

        nettyServer.sendMessage(channel, json);

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        nettyServer.getCanHandleFutures().put(requestId, future);
        return future;
    }

    public CompletableFuture<Map<String, String>> sendGetPlaceholdersRequest(Channel channel, String playerName, List<String> placeholders) {
        String requestId = UUID.randomUUID().toString();
        GetPlaceholdersRequest req = new GetPlaceholdersRequest("get_placeholders", playerName, placeholders, requestId);
        String json = gson.toJson(req);

        nettyServer.sendMessage(channel, json);

        CompletableFuture<PlaceholdersResponse> futureResp = new CompletableFuture<>();
        nettyServer.getPlaceholderFutures().put(requestId, futureResp);

        return futureResp.thenApply(PlaceholdersResponse::values);
    }
}
