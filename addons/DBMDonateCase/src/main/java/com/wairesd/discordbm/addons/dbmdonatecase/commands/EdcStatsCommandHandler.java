package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.wairesd.dceverydaycase.api.DailyCaseApi;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import com.wairesd.discordbm.api.message.ResponseType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EdcStatsCommandHandler implements CommandHandler {
    private final DailyCaseApi edcApi;
    private final DBMAPI dbmApi;
    private final Messages messages;

    public EdcStatsCommandHandler(DailyCaseApi edcApi, DBMAPI dbmApi, Messages messages) {
        this.edcApi = edcApi;
        this.dbmApi = dbmApi;
        this.messages = messages;
    }

    @Override
    public void handleCommand(String command, Map<String, String> opts, String reqId) {
        dbmApi.setResponseType(ResponseType.REPLY);
        try {
            String player = opts.getOrDefault("player", "");
            MessageSender sender = dbmApi.getMessageSender();
            
            if (player.isEmpty()) {
                sender.sendResponse(reqId, messages.get("no_player"));
                return;
            }

            long nextClaimTime = edcApi.getNextClaimTime(player);
            long currentTime = System.currentTimeMillis();
            boolean isPending = edcApi.isPending(player);
            long cooldown = edcApi.getClaimCooldown();
            String caseName = edcApi.getCaseName();
            int keysAmount = edcApi.getKeysAmount();

            StringBuilder sb = new StringBuilder();
            sb.append("**").append(messages.get("edc_info_title")).append("**\n\n");
            sb.append("**").append(messages.get("edc_case_label")).append("** ").append(caseName).append("\n");
            sb.append("**").append(messages.get("edc_keys_amount_label")).append("** ").append(keysAmount).append("\n");
            sb.append("**").append(messages.get("edc_cooldown_label")).append("** ").append(TimeUnit.MILLISECONDS.toHours(cooldown)).append(" часов\n\n");

            if (nextClaimTime > currentTime) {
                long timeLeft = nextClaimTime - currentTime;
                long hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60;
                sb.append("**").append(messages.get("edc_time_until_next_label")).append("** ").append(hours).append("ч ").append(minutes).append("м\n");
            } else {
                sb.append("**").append(messages.get("edc_status_available")).append("**\n");
            }

            if (isPending) {
                sb.append("**").append(messages.get("edc_status_pending")).append("**\n");
            }

            Map<String, String> ph = new HashMap<>();
            ph.put("player", player);
            var embed = dbmApi.createEmbedBuilder()
                .setTitle(messages.get("edc_stats_title", ph))
                .setDescription(sb.toString())
                .build();
            sender.sendResponse(reqId, embed);
            
        } catch (Exception e) {
            dbmApi.clearResponseType();
            Map<String, String> ph = new HashMap<>();
            ph.put("error", e.getMessage());
            dbmApi.getMessageSender().sendResponse(reqId, messages.get("edc_error_stats", ph));
        }
    }
} 