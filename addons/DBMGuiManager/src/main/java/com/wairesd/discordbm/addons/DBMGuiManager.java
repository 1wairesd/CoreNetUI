package com.wairesd.discordbm.addons;

import com.wairesd.discordbm.api.DiscordBMAPIProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.jodexindustries.jguiwrapper.common.JGuiInitializer;
import com.wairesd.discordbm.api.DiscordBMAPI;

public final class DBMGuiManager extends JavaPlugin {

    private DiscordBMAPI api;

    @Override
    public void onEnable() {
        this.api = DiscordBMAPIProvider.getInstanceOrThrow();

        JGuiInitializer.init(this);
        getCommand("opengui").setExecutor(this::onOpenGuiCommand);
    }

    @Override
    public void onDisable() {
    }

    private boolean onOpenGuiCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игрок может открыть GUI.");
            return true;
        }
        Player player = (Player) sender;
        new MenuGui(api).open(player);
        return true;
    }
}
