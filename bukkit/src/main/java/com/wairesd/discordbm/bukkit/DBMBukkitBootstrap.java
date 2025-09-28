package com.wairesd.discordbm.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import com.wairesd.discordbm.common.util.StartupTimer;

public class DBMBukkitBootstrap extends JavaPlugin {
    private final StartupTimer timer = new StartupTimer();

    @Override
    public void onEnable() {
        timer.start();

        timer.stop();
        timer.printElapsed();
    }

    @Override
    public void onDisable() {
    }
}
