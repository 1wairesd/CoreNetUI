package com.wairesd.discordbm.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.wairesd.discordbm.host.common.service.WebhookEventService;
import com.wairesd.discordbm.velocity.api.VelocityUtils;

public class PlayerQuitListener {
    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        String playerName = VelocityUtils.getPlayerName(player);
        String playerIp = player.getRemoteAddress().getAddress().getHostAddress();
        String reason = event.getLoginStatus().name();
        WebhookEventService.handlePlayerQuitEvent(playerName, playerIp, reason);
    }
}
