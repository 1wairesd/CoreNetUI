package com.wairesd.discordbm.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;

@Plugin(id = "discordbm", name = "DiscordBM", version = "1.0", authors = {"wairesd"})
public class DBMVelocityBootstrap {

    @Inject
    public DBMVelocityBootstrap() {
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
    }
}