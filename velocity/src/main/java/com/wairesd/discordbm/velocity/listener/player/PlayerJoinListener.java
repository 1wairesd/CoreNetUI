package com.wairesd.discordbm.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.wairesd.discordbm.host.common.service.WebhookEventService;
import com.wairesd.discordbm.velocity.api.VelocityUtils;

public class PlayerJoinListener {
    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        Player player = event.getPlayer();
        String playerName = VelocityUtils.getPlayerName(player);
        String playerIp = player.getRemoteAddress().getAddress().getHostAddress();
        WebhookEventService.handlePlayerJoinEvent(playerName, playerIp);
    }
}