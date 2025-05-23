package com.wairesd.discordbm.bukkit.network;

import com.google.gson.Gson;
import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import com.wairesd.discordbm.bukkit.models.command.Command;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.register.RegisterMessage;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;

import java.net.InetSocketAddress;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

/**
 * The NettyService class provides functionality for interacting with a Netty server,
 * enabling communication between the DiscordBMB plugin and a Velocity server through
 * networking. This service manages a Netty client, handles sending various types of
 * messages, and provides connection lifecycle management.
 */
public class NettyService {
    private final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());
    private final DiscordBMB plugin;
    private final Gson gson = new Gson();
    private NettyClient nettyClient;

    public NettyService(DiscordBMB plugin) {
        this.plugin = plugin;
    }

    public void initializeNettyClient(String host, int port) {
        nettyClient = new NettyClient(new InetSocketAddress(host, port), plugin);
        try {
            nettyClient.connect();
        } catch (Exception e) {
            if (Settings.isDebugErrors()) {
                pluginLogger.warn("Failed to connect to Velocity Netty server: " + e.getMessage());
            }
        }
    }

    public void closeNettyConnection() {
        if (nettyClient != null && nettyClient.isActive()) {
            nettyClient.close();
            nettyClient = null;
            if (Settings.isDebugConnections()) {
                pluginLogger.info("Netty connection closed.");
            }
        }
    }

    public void sendResponse(String requestId, String embedJson) {
        if (nettyClient != null && nettyClient.isActive()) {
            EmbedDefinition embedObj = gson.fromJson(embedJson, EmbedDefinition.class);
            ResponseMessage respMsg = new ResponseMessage("response", requestId, null, embedObj);
            nettyClient.send(gson.toJson(respMsg));
        }
    }

    public void sendNettyMessage(String message) {
        if (nettyClient != null && nettyClient.isActive()) {
            nettyClient.send(message);
        } else {
            if (Settings.isDebugErrors()) {
                pluginLogger.warn("Netty connection not active. Message not sent: " + message);
            }
        }
    }

    public void sendAllAddonCommands(List<Command> addonCommands, String serverName) {
        if (nettyClient != null && nettyClient.isActive() && !addonCommands.isEmpty()) {
            String secret = Settings.getSecretCode();
            RegisterMessage<Command> msg = new RegisterMessage<>(
                    "register", serverName, plugin.getName(), addonCommands, secret
            );
            nettyClient.send(gson.toJson(msg));
            if (Settings.isDebugCommandRegistrations()) {
                pluginLogger.info(
                        "Sent registration message for " + addonCommands.size() + " addon commands."
                );
            }
        }
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }
}
