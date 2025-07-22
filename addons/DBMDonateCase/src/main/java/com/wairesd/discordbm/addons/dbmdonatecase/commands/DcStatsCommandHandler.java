package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import java.util.HashMap;
import java.util.Map;

public class DcStatsCommandHandler implements CommandHandler {
    private final DCAPI api;
    private final DBMAPI dbmApi;
    private final CaseKeyManager keyManager;
    private final CaseOpenManager openManager;
    private final Messages messages;

    public DcStatsCommandHandler(DCAPI api, DBMAPI dbmApi, CaseKeyManager keyManager, CaseOpenManager openManager, Messages messages) {
        this.api = api;
        this.dbmApi = dbmApi;
        this.keyManager = keyManager;
        this.openManager = openManager;
        this.messages = messages;
    }

    @Override
    public void handleCommand(String command, Map<String, String> opts, String reqId) {
        String player = opts.getOrDefault("player", "");
        MessageSender sender = dbmApi.getMessageSender();
        if (player.isEmpty()) {
            sender.sendResponse(reqId, messages.get("no_player"));
            return;
        }
        keyManager.getAsync(player).thenCombine(
            openManager.getAsync(player),
            (keys, opened) -> {
                if ((keys == null || keys.isEmpty()) && (opened == null || opened.isEmpty())) {
                    Map<String, String> ph = new HashMap<>();
                    ph.put("player", player);
                    return messages.get("no_player_data", ph);
                }
                StringBuilder sb = new StringBuilder();
                if (keys != null && !keys.isEmpty()) {
                    sb.append(messages.get("keys_section")).append("\n");
                    keys.forEach((caseType, amount) -> {
                        String displayName = api.getCaseManager().getByType(caseType)
                            .map(def -> def.settings().displayName())
                            .orElse(caseType);
                        sb.append("- ").append(displayName).append(": ").append(amount).append("\n");
                    });
                }
                if (opened != null && !opened.isEmpty()) {
                    sb.append(messages.get("opened_section")).append("\n");
                    opened.forEach((caseType, amount) -> {
                        String displayName = api.getCaseManager().getByType(caseType)
                            .map(def -> def.settings().displayName())
                            .orElse(caseType);
                        sb.append("- ").append(displayName).append(": ").append(amount).append("\n");
                    });
                }
                return sb.toString();
            }
        ).thenAccept(result -> {
            if (result == null || result.startsWith("У игрока ")) {
                Map<String, String> ph = new HashMap<>();
                ph.put("player", player);
                sender.sendResponse(reqId, messages.get("no_player_data", ph));
            } else {
                Map<String, String> ph = new HashMap<>();
                ph.put("player", player);
                var embed = dbmApi.createEmbedBuilder()
                    .setTitle(messages.get("stats_title", ph))
                    .setDescription(result)
                    .build();
                sender.sendResponse(reqId, embed);
            }
        });
    }
} 