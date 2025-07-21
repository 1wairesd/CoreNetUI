package com.wairesd.discordbm.bukkit.api;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitUtils {
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }
    public static void runTaskAsynchronously(JavaPlugin plugin, Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }
    public static void runTaskLaterAsynchronously(JavaPlugin plugin, Runnable task, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }
} 