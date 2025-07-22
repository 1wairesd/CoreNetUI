package com.wairesd.discordbm.addons.dbmguimanager;

import com.jodexindustries.jguiwrapper.common.JGuiInitializer;
import com.wairesd.discordbm.addons.dbmguimanager.menu.MainMenu;
import com.wairesd.discordbm.api.DBMAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DBMGuiManager extends JavaPlugin {

    private DBMAPI api;

    @Override
    public void onEnable() {
        this.api = DBMAPI.getInstance();

        saveDefaultConfig();

        JGuiInitializer.init(this);

        PluginCommand command = getCommand("opengui");
        if (command != null) command.setExecutor(this);
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
