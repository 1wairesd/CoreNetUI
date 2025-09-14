package com.wairesd.discordbm.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;

public class PlayerQuitListener {
    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
    }
}
