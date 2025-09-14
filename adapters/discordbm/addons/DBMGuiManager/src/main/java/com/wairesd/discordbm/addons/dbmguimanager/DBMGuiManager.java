package com.wairesd.discordbm.addons.dbmguimanager;

import com.jodexindustries.jguiwrapper.api.placeholder.PlaceholderEngine;
import com.jodexindustries.jguiwrapper.common.JGuiInitializer;
import com.wairesd.discordbm.addons.dbmguimanager.menu.MainMenu;
import com.wairesd.discordbm.addons.dbmguimanager.menu.command.CommandItemHandler;
import com.wairesd.discordbm.api.DBMAPI;
import net.kyori.adventure.key.Key;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DBMGuiManager extends JavaPlugin {

    private DBMAPI api;
    private PlaceholderEngine placeholderEngine;

    public static final Key COMMAND_ITEM_KEY = Key.key("dbm", "command");

    @Override
    public void onEnable() {
        this.api = DBMAPI.getInstance();

        saveDefaultConfig();

        JGuiInitializer.init(this);

        JGuiInitializer.get().getRegistry().registerHandler(COMMAND_ITEM_KEY, new CommandItemHandler());

        initPlaceholders();

        PluginCommand command = getCommand("opengui");
        if (command != null) command.setExecutor(this);
    }

    private void initPlaceholders() {
        placeholderEngine = PlaceholderEngine.of();

        placeholderEngine.register("%uptime%", player -> formatUptime(api.getUptimeMillis()));
    }

    private String formatUptime(long uptimeMillis) {
        String format = getConfig().getString("uptime-format", "short");
        long seconds = uptimeMillis / 1000 % 60;
        long minutes = uptimeMillis / (1000 * 60) % 60;
        long hours = uptimeMillis / (1000 * 60 * 60) % 24;
        long days = uptimeMillis / (1000 * 60 * 60 * 24);
        
        if (format.equalsIgnoreCase("long")) {
            return String.format("%d дней %d часов %d минут %d секунд", days, hours, minutes, seconds);
        } else {
            return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        }
    }

    public PlaceholderEngine getPlaceholderEngine() {
        return placeholderEngine;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько игрок может открыть GUI.");
            return false;
        }
        new MainMenu(this, api).open(player);
        return true;
    }
}
