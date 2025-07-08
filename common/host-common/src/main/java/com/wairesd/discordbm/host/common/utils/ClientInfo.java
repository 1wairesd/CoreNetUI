package com.wairesd.discordbm.host.common.utils;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientInfo {
    public final String name;
    public final String ip;
    public final int port;
    public final long uptimeMillis;

    public ClientInfo(String name, String ip, int port, long uptimeMillis) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.uptimeMillis = uptimeMillis;
    }

    public static List<ClientInfo> getActiveClientsInfo(Map<Channel, String> channelToServerName, Map<Channel, Long> channelConnectTime) {
        List<ClientInfo> result = new ArrayList<>();
        for (Map.Entry<Channel, String> entry : channelToServerName.entrySet()) {
            Channel channel = entry.getKey();
            String name = entry.getValue();
            String ip = "unknown";
            int port = -1;
            try {
                java.net.InetSocketAddress addr = (java.net.InetSocketAddress) channel.remoteAddress();
                ip = addr.getHostString();
                port = addr.getPort();
            } catch (Exception ignored) {}
            Long connectTime = channelConnectTime.get(channel);
            long uptime = connectTime != null ? System.currentTimeMillis() - connectTime : 0L;
            result.add(new ClientInfo(name, ip, port, uptime));
        }
        return result;
    }
}
