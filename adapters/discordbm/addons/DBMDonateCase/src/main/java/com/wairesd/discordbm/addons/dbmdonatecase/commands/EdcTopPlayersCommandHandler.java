package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import com.wairesd.discordbm.api.message.ResponseType;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class EdcTopPlayersCommandHandler implements CommandHandler {
    private final Object edcApi;
    private final DBMAPI dbmApi;
    private final Messages messages;

    public EdcTopPlayersCommandHandler(Object edcApi, DBMAPI dbmApi, Messages messages) {
        this.edcApi = edcApi;
        this.dbmApi = dbmApi;
        this.messages = messages;
    }

    @Override
    public void handleCommand(String command, Map<String, String> opts, String reqId) {
        dbmApi.setResponseType(ResponseType.REPLY);
        try {
            MessageSender sender = dbmApi.getMessageSender();
            
            @SuppressWarnings("unchecked")
            Map<String, Long> allClaimTimes = (Map<String, Long>) edcApi.getClass().getMethod("getAllNextClaimTimes").invoke(edcApi);
            long currentTime = System.currentTimeMillis();
            
            List<PlayerClaimInfo> playerInfos = new ArrayList<>();
            
            for (Map.Entry<String, Long> entry : allClaimTimes.entrySet()) {
                String player = entry.getKey();
                long nextClaimTime = entry.getValue();
                
                long timeSinceLastClaim = currentTime - nextClaimTime;
                if (timeSinceLastClaim > 0) {
                    long daysSinceClaim = TimeUnit.MILLISECONDS.toDays(timeSinceLastClaim);
                    playerInfos.add(new PlayerClaimInfo(player, daysSinceClaim));
                }
            }
            
            playerInfos.sort((a, b) -> Long.compare(b.daysSinceClaim, a.daysSinceClaim));
            
            StringBuilder sb = new StringBuilder();
            sb.append("**").append(messages.get("edc_top_players_header")).append("**\n\n");
            
            int count = 0;
            for (PlayerClaimInfo info : playerInfos) {
                if (count >= 10) break;
                count++;
                Map<String, String> ph = new HashMap<>();
                ph.put("index", String.valueOf(count));
                ph.put("player", info.playerName);
                ph.put("days", String.valueOf(info.daysSinceClaim));
                sb.append(messages.get("edc_top_entry", ph)).append("\n");
            }
            
            if (playerInfos.isEmpty()) {
                sb.append(messages.get("edc_no_data"));
            }

            var embed = dbmApi.createEmbedBuilder()
                .setTitle(messages.get("edc_top_players_title"))
                .setDescription(sb.toString())
                .build();
            sender.sendResponse(reqId, embed);
            
        } catch (Exception e) {
            dbmApi.clearResponseType();
            Map<String, String> ph = new HashMap<>();
            ph.put("error", e.getMessage());
            dbmApi.getMessageSender().sendResponse(reqId, messages.get("edc_error_top_players", ph));
        }
    }
    
    private static class PlayerClaimInfo {
        final String playerName;
        final long daysSinceClaim;
        
        PlayerClaimInfo(String playerName, long daysSinceClaim) {
            this.playerName = playerName;
            this.daysSinceClaim = daysSinceClaim;
        }
    }
} 