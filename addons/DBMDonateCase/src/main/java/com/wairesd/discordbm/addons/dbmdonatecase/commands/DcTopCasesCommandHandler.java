package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.jodexindustries.donatecase.api.DCAPI;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DcTopCasesCommandHandler implements CommandHandler {
    private final DCAPI api;
    private final DBMAPI dbmApi;
    private final Messages messages;

    public DcTopCasesCommandHandler(DCAPI api, DBMAPI dbmApi, Messages messages) {
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
            Map<String, Long> caseCount = historyList.stream()
                .collect(Collectors.groupingBy(
                    h -> h.caseType(), Collectors.counting()
                ));
            var top = caseCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .toList();
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (var entry : top) {
                String displayName = api.getCaseManager().getByType(entry.getKey())
                    .map(def -> def.settings().displayName())
                    .orElse(entry.getKey());
                Map<String, String> ph = new HashMap<>();
                ph.put("index", String.valueOf(i++));
                ph.put("name", displayName);
                ph.put("count", String.valueOf(entry.getValue()));
                sb.append(messages.get("top_entry", ph)).append("\n");
            }
            var embed = dbmApi.createEmbedBuilder()
                .setTitle(messages.get("top_cases_title"))
                .setDescription(sb.toString())
                .build();
            sender.sendResponse(reqId, embed);
        });
    }
} 