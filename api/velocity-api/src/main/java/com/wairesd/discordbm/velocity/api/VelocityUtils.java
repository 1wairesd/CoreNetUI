package com.wairesd.discordbm.velocity.api;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.Optional;
import java.util.UUID;

public class VelocityUtils {
    public static String getPlayerName(Player player) {
        return player.getUsername();
    }

    public static UUID getPlayerUUID(Player player) {
        return player.getUniqueId();
    }

    public static Optional<Player> findPlayer(ProxyServer proxy, UUID uuid) {
        return proxy.getPlayer(uuid);
    }

    public static Optional<Player> findPlayer(ProxyServer proxy, String name) {
        return proxy.getPlayer(name);
    }
} 