package com.wairesd.discordbm.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.wairesd.discordbm.common.util.StartupTimer;

@Plugin(id = "discordbm",
        name = "DiscordBM",
        version = "1.0",
        authors = {"wairesd"})

public class DBMVelocityBootstrap {
    private final StartupTimer timer = new StartupTimer();

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        timer.start();

        timer.stop();
        timer.printElapsed();
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
    }
}