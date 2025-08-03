package com.wairesd.discordbm.bukkit.listener.player;

import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.client.common.service.ClientWebhookEventService;
import com.wairesd.discordbm.bukkit.api.BukkitUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class PlayerQuitListener implements Listener {
    private final Platform platform;
    private final ClientWebhookEventService webhookService;

    public PlayerQuitListener(Platform platform) {
        this.platform = platform;
        this.webhookService = new ClientWebhookEventService(platform);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = BukkitUtils.getPlayerName(player);
        String playerIp = BukkitUtils.getPlayerIp(player);
        String reason = "NORMAL";
        
        if (playerIp == null) {
            playerIp = "unknown";
        }
        
        webhookService.handlePlayerQuitEvent(playerName, playerIp, reason);
    }
} 