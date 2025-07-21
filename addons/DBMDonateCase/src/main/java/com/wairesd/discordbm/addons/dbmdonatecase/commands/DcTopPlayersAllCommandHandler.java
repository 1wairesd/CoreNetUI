package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.jodexindustries.donatecase.api.DCAPI;
import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DcTopPlayersAllCommandHandler implements CommandHandler {
    private final DCAPI api;
    private final DiscordBMAPI dbmApi;
    private final Messages messages;

    public DcTopPlayersAllCommandHandler(DCAPI api, DiscordBMAPI dbmApi, Messages messages) {
        this.api = api;
        this.dbmApi = dbmApi;
        this.messages = messages;
    }

    @Override
    public void handleCommand(String command, Map<String, String> opts, String reqId) {
        MessageSender sender = dbmApi.getMessageSender();
        api.getDatabase().getHistoryData().thenAccept(historyList -> {
            if (historyList == null || historyList.isEmpty()) {
                sender.sendResponse(reqId, messages.get("no_case_data"));
                return;
            }
            java.util.Set<String> allPlayers = historyList.stream()
                    .map(h -> h.playerName())
                    .collect(Collectors.toSet());
            Map<String, Integer> playerCount = new HashMap<>();
            for (String player : allPlayers) {
                Map<String, Integer> openMap = api.getCaseOpenManager().get(player);
                int sum = 0;
                for (Map.Entry<String, Integer> entry : openMap.entrySet()) {
                    sum += entry.getValue();
                }
                playerCount.put(player, sum);
            }
            var top = playerCount.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .limit(10)
                    .toList();
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (var entry : top) {
                Map<String, String> ph = new HashMap<>();
                ph.put("index", String.valueOf(i++));
                ph.put("name", entry.getKey());
                ph.put("count", String.valueOf(entry.getValue()));
                sb.append(messages.get("top_entry", ph)).append("\n");
            }
            var embed = dbmApi.createEmbedBuilder()
                    .setTitle(messages.get("top_players_all_title"))
                    .setDescription(sb.toString())
                    .build();
            sender.sendResponse(reqId, embed);
        });
    }
} 