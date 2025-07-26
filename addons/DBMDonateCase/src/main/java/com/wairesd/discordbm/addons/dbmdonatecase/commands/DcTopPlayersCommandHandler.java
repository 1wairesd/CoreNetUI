package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.jodexindustries.donatecase.api.DCAPI;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.api.message.ResponseType;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DcTopPlayersCommandHandler implements CommandHandler {
    private final DCAPI api;
    private final DBMAPI dbmApi;
    private final Messages messages;

    public DcTopPlayersCommandHandler(DCAPI api, DBMAPI dbmApi, Messages messages) {
        this.api = api;
        this.dbmApi = dbmApi;
        this.messages = messages;
    }

    @Override
    public void handleCommand(String command, Map<String, String> opts, String reqId) {
        dbmApi.setResponseType(ResponseType.REPLY);
        
        String caseType = opts.getOrDefault("case", "");
        MessageSender sender = dbmApi.getMessageSender();
        if (caseType.isEmpty()) {
            sender.sendResponse(reqId, messages.get("no_case_type"));
            dbmApi.clearResponseType();
            return;
        }
        api.getDatabase().getHistoryData(caseType).thenAccept(historyList -> {
            if (historyList == null || historyList.isEmpty()) {
                Map<String, String> ph = new HashMap<>();
                ph.put("caseType", caseType);
                sender.sendResponse(reqId, messages.get("no_case_data_specific", ph));
                dbmApi.clearResponseType();
                return;
            }
            Map<String, Long> playerCount = historyList.stream()
                .collect(Collectors.groupingBy(
                    h -> h.playerName(), Collectors.counting()
                ));
            var top = playerCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .toList();
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (var entry : top) {
                Map<String, String> phEntry = new HashMap<>();
                phEntry.put("index", String.valueOf(i++));
                phEntry.put("name", entry.getKey());
                phEntry.put("count", String.valueOf(entry.getValue()));
                sb.append(messages.get("top_entry", phEntry)).append("\n");
            }
            String displayName = api.getCaseManager().getByType(caseType)
                .map(def -> def.settings().displayName())
                .orElse(caseType);
            Map<String, String> ph = new HashMap<>();
            ph.put("caseType", displayName);
            var embed = dbmApi.createEmbedBuilder()
                .setTitle(messages.get("top_players_title", ph))
                .setDescription(sb.toString())
                .build();
            sender.sendResponse(reqId, embed);
            dbmApi.clearResponseType();
        });
    }
} 