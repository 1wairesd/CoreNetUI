package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.utils.ClientInfo;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ClientsCommand {
    private final DiscordBMHPlatformManager platformManager;

    public ClientsCommand(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    public void execute(CommandSource source, MessageContext context) {
        if (!source.hasPermission("discordbotmanager.clients")) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_PERMISSION, context));
            return;
        }
        NettyServer nettyServer = platformManager.getNettyServer();
        if (nettyServer == null) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_ACTIVE_CLIENTS, context));
            return;
        }
        List<ClientInfo> clients = nettyServer.getActiveClientsInfo();
        if (clients.isEmpty()) {
            source.sendMessage(Messages.getComponent(Messages.Keys.NO_CONNECTED_CLIENTS, context));
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Clients (").append(clients.size()).append("):");
        for (ClientInfo client : clients) {
            String uptimeStr = formatUptime(client.uptimeMillis);
            sb.append("\n- ").append(client.name)
              .append(" (").append(client.ip).append(":").append(client.port).append(") time: ")
              .append(uptimeStr);
        }
        source.sendMessage(Component.text(sb.toString()));
    }

    private String formatUptime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");
        return sb.toString();
    }
} 