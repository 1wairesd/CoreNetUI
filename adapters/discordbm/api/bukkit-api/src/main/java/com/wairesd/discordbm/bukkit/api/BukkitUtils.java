package com.wairesd.discordbm.bukkit.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Utility class for common Bukkit operations
 */
public class BukkitUtils {
    
    /**
     * Sends a message to a command sender
     * 
     * @param sender The command sender
     * @param message The message to send
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
    
    /**
     * Checks if a command sender has a specific permission
     * 
     * @param sender The command sender
     * @param permission The permission to check
     * @return True if the sender has the permission
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }
    
    /**
     * Runs a task asynchronously
     * 
     * @param plugin The plugin instance
     * @param task The task to run
     */
    public static void runTaskAsynchronously(JavaPlugin plugin, Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }
    
    /**
     * Runs a task later asynchronously
     * 
     * @param plugin The plugin instance
     * @param task The task to run
     * @param delay The delay in ticks
     */
    public static void runTaskLaterAsynchronously(JavaPlugin plugin, Runnable task, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }
    
    /**
     * Gets a player by name
     * 
     * @param name The player name
     * @return The player or null if not found
     */
    public static Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }
    
    /**
     * Gets a player by UUID
     * 
     * @param uuid The player UUID
     * @return The player or null if not found
     */
    public static Player getPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
    
    /**
     * Gets the player name
     * 
     * @param player The player
     * @return The player name
     */
    public static String getPlayerName(Player player) {
        return player.getName();
    }
    
    /**
     * Gets the player UUID
     * 
     * @param player The player
     * @return The player UUID
     */
    public static UUID getPlayerUUID(Player player) {
        return player.getUniqueId();
    }
    
    /**
     * Gets the online player count
     * 
     * @return The number of online players
     */
    public static int getOnlinePlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }
    
    /**
     * Gets the maximum player count
     * 
     * @return The maximum number of players
     */
    public static int getMaxPlayerCount() {
        return Bukkit.getMaxPlayers();
    }
    
    /**
     * Gets the player IP address
     * 
     * @param player The player
     * @return The player IP address or null if not available
     */
    public static String getPlayerIp(Player player) {
        return player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : null;
    }
    
    /**
     * Gets the player display name (can be different from actual name)
     * 
     * @param player The player
     * @return The player display name
     */
    public static String getPlayerDisplayName(Player player) {
        return player.getDisplayName();
    }
} 