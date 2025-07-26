package com.wairesd.discordbm.velocity.api;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for common Velocity operations
 */
public class VelocityUtils {
    /**
     * Gets the username of a player
     *
     * @param player The player
     * @return The player's username
     */
    public static String getPlayerName(Player player) {
        return player.getUsername();
    }

    /**
     * Gets the UUID of a player
     *
     * @param player The player
     * @return The player's UUID
     */
    public static UUID getPlayerUUID(Player player) {
        return player.getUniqueId();
    }

    /**
     * Finds a player by UUID
     *
     * @param proxy The proxy server
     * @param uuid The player's UUID
     * @return Optional containing the player if found
     */
    public static Optional<Player> findPlayer(ProxyServer proxy, UUID uuid) {
        return proxy.getPlayer(uuid);
    }

    /**
     * Finds a player by username
     *
     * @param proxy The proxy server
     * @param name The player's username
     * @return Optional containing the player if found
     */
    public static Optional<Player> findPlayer(ProxyServer proxy, String name) {
        return proxy.getPlayer(name);
    }
} 