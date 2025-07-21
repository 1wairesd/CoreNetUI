package com.wairesd.discordbm.addons.dbmdonatecase.listener;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.jodexindustries.donatecase.api.data.casedefinition.CaseItem;
import com.jodexindustries.donatecase.api.event.Subscriber;
import com.jodexindustries.donatecase.api.event.player.OpenCaseEvent;
import com.jodexindustries.donatecase.api.event.animation.AnimationEndEvent;
import com.jodexindustries.donatecase.api.data.ActiveCase;
import com.jodexindustries.donatecase.api.manager.CaseManager;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;
import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.WebhookTriggersConfig;
import net.kyori.event.method.annotation.Subscribe;

import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

public class CaseOpenListener implements Subscriber {
    private final DiscordBMAPI dbmApi;
    private final Logger logger;
    private final WebhookTriggersConfig triggersConfig;
    private final CaseOpenManager openManager;
    private final CaseManager caseManager;

    public CaseOpenListener(DiscordBMAPI dbmApi, Logger logger, WebhookTriggersConfig triggersConfig, CaseOpenManager openManager, CaseManager caseManager) {
        this.dbmApi = dbmApi;
        this.logger = logger;
        this.triggersConfig = triggersConfig;
        this.openManager = openManager;
        this.caseManager = caseManager;
    }

    @Subscribe
    public void onOpenCase(OpenCaseEvent event) {
        String player = event.player().getName();
        String caseType = event.definition().settings().type();
        String caseName = event.definition().settings().displayName().replaceAll("[§&][0-9a-fk-or]", "");
        int playerOpenCount = openManager.get(caseType, player);
        long now = System.currentTimeMillis();
        for (WebhookTriggersConfig.Trigger trigger : triggersConfig.getEnabledTriggers()) {
            if (trigger.type.equals("player_open")) {
                boolean playerMatch = trigger.player != null && trigger.player.equalsIgnoreCase(player);
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseType);
                if (playerMatch && caseMatch) {
                    String msg = trigger.message.replace("{player}", player).replace("{case}", caseName);
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
            if (trigger.type.equals("first_open")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseType);
                if (caseMatch && playerOpenCount == 1) {
                    String msg = trigger.message.replace("{player}", player).replace("{case}", caseName);
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
        }
    }

    @Subscribe
    public void onDrop(AnimationEndEvent event) {
        ActiveCase ac = event.activeCase();
        String player = ac.player().getName();
        String caseType = ac.caseType();
        String caseName = caseManager.get(caseType) != null ? caseManager.get(caseType).caseDisplayName().replaceAll("[§&][0-9a-fk-or]", "") : caseType;
        CaseItem winItem = ac.winItem();
        String dropName = winItem != null ? winItem.name() : "";
        double dropChance = winItem != null ? winItem.chance() : 0.0;

        boolean needGlobalCount = false, needPlayerCount = false, needHistory = false;
        for (WebhookTriggersConfig.Trigger trigger : triggersConfig.getEnabledTriggers()) {
            if (!trigger.enabled) continue;
            switch (trigger.type) {
                case "global_open_count": needGlobalCount = true; break;
                case "case_open_count": needPlayerCount = true; break;
                case "open_in_period": needHistory = true; break;
            }
        }

        int globalOpenCount = 0;
        if (needGlobalCount) {
            try {
                Map<String, Map<String, Integer>> globalMap = openManager.getGlobalAsync().join();
                for (Map<String, Integer> caseMap : globalMap.values()) {
                    for (int count : caseMap.values()) {
                        globalOpenCount += count;
                    }
                }
            } catch (Exception ignored) {}
        }
        int playerOpenCount = 0;
        if (needPlayerCount) {
            try {
                Map<String, Integer> caseMap = openManager.getGlobalAsync(caseType).join();
                for (int count : caseMap.values()) {
                    playerOpenCount += count;
                }
            } catch (Exception ignored) {}
        }
        List<CaseData.History> historyList = null;
        if (needHistory) {
            try {
                historyList = DCAPI.getInstance().getDatabase().getHistoryData().join();
            } catch (Exception ignored) {}
        }

        for (WebhookTriggersConfig.Trigger trigger : triggersConfig.getEnabledTriggers()) {
            if (!trigger.enabled) continue;
            if (trigger.type.equals("drop_obtained")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseType);
                boolean dropMatch = trigger.drop != null && dropName.equalsIgnoreCase(trigger.drop);
                if (caseMatch && dropMatch) {
                    String msg = trigger.message.replace("{player}", player).replace("{case}", caseName).replace("{drop}", dropName);
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
            if (trigger.type.equals("rare_drop")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseType);
                boolean chanceMatch = trigger.chanceBelow != null && dropChance <= trigger.chanceBelow;
                if (caseMatch && chanceMatch) {
                    String msg = trigger.message.replace("{player}", player).replace("{case}", caseName).replace("{drop}", dropName).replace("{chance}", String.valueOf(dropChance));
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
            if (trigger.type.equals("global_open_count")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseType);
                if (caseMatch && trigger.count != null && globalOpenCount == trigger.count) {
                    String msg = trigger.message.replace("{case}", caseName).replace("{count}", String.valueOf(globalOpenCount));
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
            if (trigger.type.equals("case_open_count")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseType);
                if (caseMatch && trigger.count != null && playerOpenCount == trigger.count) {
                    String msg = trigger.message.replace("{player}", player).replace("{case}", caseName).replace("{count}", String.valueOf(playerOpenCount));
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
            if (trigger.type.equals("open_in_period") && historyList != null) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseType);
                if (caseMatch && trigger.count != null && trigger.period != null) {
                    int opensInPeriod = 0;
                    long now = System.currentTimeMillis();
                    long periodStart = 0L;
                    switch (trigger.period.toLowerCase()) {
                        case "day": periodStart = now - 24L * 60 * 60 * 1000; break;
                        case "week": periodStart = now - 7L * 24 * 60 * 60 * 1000; break;
                        case "month": periodStart = now - 30L * 24 * 60 * 60 * 1000; break;
                        default: periodStart = 0L;
                    }
                    for (CaseData.History h : historyList) {
                        if (h.playerName() != null && h.playerName().equalsIgnoreCase(player) && h.time() >= periodStart) {
                            if (trigger.caseName.equals("*") || (h.caseType() != null && h.caseType().equalsIgnoreCase(caseType))) {
                                opensInPeriod++;
                            }
                        }
                    }
                    if (opensInPeriod == trigger.count) {
                        String msg = trigger.message.replace("{player}", player).replace("{case}", caseName).replace("{count}", String.valueOf(opensInPeriod));
                        dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                    }
                }
            }
        }
    }
} 