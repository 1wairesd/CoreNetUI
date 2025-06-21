package com.wairesd.discordbm.host.common.commandbuilder.core.channel;

import com.wairesd.discordbm.host.common.network.NettyServer;
import io.netty.channel.Channel;

public class ChannelFinder {
    private final NettyServer nettyServer;

    public ChannelFinder(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public Channel findChannelForServer(String serverName) {
        for (var entry : nettyServer.getChannelToServerName().entrySet()) {
            if (entry.getValue().equals(serverName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
