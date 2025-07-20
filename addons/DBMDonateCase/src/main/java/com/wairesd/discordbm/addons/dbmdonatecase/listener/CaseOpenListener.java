package com.wairesd.discordbm.addons.dbmdonatecase.listener;

import com.jodexindustries.donatecase.api.data.casedefinition.CaseDefinition;
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

import java.util.Optional;
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

        openManager.getAsync(caseType, player).thenAccept(playerOpenCount -> {
            long now = System.currentTimeMillis();
            for (WebhookTriggersConfig.Trigger trigger : triggersConfig.getEnabledTriggers()) {
                if (trigger.type.equals("player_open")) {
                    boolean playerMatch = trigger.player != null && trigger.player.equalsIgnoreCase(player);
                    boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseName);
                    if (playerMatch && caseMatch) {
                        String msg = trigger.message.replace("{player}", player).replace("{case}", caseName);
                        dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                    }
                }
                if (trigger.type.equals("case_open_count")) {
                    boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseName);
                    if (caseMatch && playerOpenCount.equals(trigger.count)) {
                        String msg = trigger.message.replace("{player}", player).replace("{case}", caseName).replace("{count}", String.valueOf(playerOpenCount));
                        dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                    }
                }
                if (trigger.type.equals("first_open")) {
                    boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseName);
                    if (caseMatch && playerOpenCount == 1) {
                        String msg = trigger.message.replace("{player}", player).replace("{case}", caseName);
                        dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                    }
                }
            }
        });
    }

    @Subscribe
    public void onDrop(AnimationEndEvent event) {
        ActiveCase ac = event.activeCase();
        String player = ac.player().getName();
        String caseType = ac.caseType();

        Optional<CaseDefinition> optional = caseManager.getByType(caseType);

        String caseName = optional.map(caseDefinition -> caseDefinition.settings().displayName().replaceAll("[§&][0-9a-fk-or]", "")).orElse(caseType);
        CaseItem winItem = ac.winItem();
        String dropName = winItem != null ? winItem.name() : "";
        double dropChance = winItem != null ? winItem.chance() : 0.0;
        int globalOpenCount = 0;
        try {
            Map<String, Map<String, Integer>> globalMap = openManager.getGlobalAsync().join();
            for (Map<String, Integer> caseMap : globalMap.values()) {
                for (int count : caseMap.values()) {
                    globalOpenCount += count;
                }
            }
            logger.warning("DEBUG: globalOpenCount (getGlobalAsync) = " + globalOpenCount);
        } catch (Exception e) {
            logger.warning("DEBUG: getGlobalAsync exception: " + e.getMessage());
        }
        for (WebhookTriggersConfig.Trigger trigger : triggersConfig.getEnabledTriggers()) {
            logger.warning("DEBUG: Checking trigger type = " + trigger.type + ", count = " + trigger.count + ", enabled = " + trigger.enabled);
            if (trigger.type.equals("drop_obtained")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseName);
                boolean dropMatch = trigger.drop != null && dropName.equalsIgnoreCase(trigger.drop);
                logger.warning("DEBUG: drop_obtained: caseName = " + caseName + ", trigger.caseName = " + trigger.caseName + ", caseMatch = " + caseMatch + ", dropMatch = " + dropMatch);
                if (caseMatch && dropMatch) {
                    String msg = trigger.message.replace("{player}", player).replace("{case}", caseName).replace("{drop}", dropName);
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
            if (trigger.type.equals("rare_drop")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseName);
                boolean chanceMatch = trigger.chanceBelow != null && dropChance < trigger.chanceBelow;
                logger.warning("DEBUG: rare_drop: caseName = " + caseName + ", trigger.caseName = " + trigger.caseName + ", caseMatch = " + caseMatch + ", chanceMatch = " + chanceMatch);
                if (caseMatch && chanceMatch) {
                    String msg = trigger.message.replace("{player}", player).replace("{case}", caseName).replace("{drop}", dropName).replace("{chance}", String.valueOf(dropChance));
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
            if (trigger.type.equals("global_open_count")) {
                boolean caseMatch = trigger.caseName.equals("*") || trigger.caseName.equalsIgnoreCase(caseName);
                logger.warning("DEBUG: global_open_count: caseName = " + caseName + ", trigger.caseName = " + trigger.caseName + ", caseMatch = " + caseMatch + ", globalOpenCount = " + globalOpenCount + ", trigger.count = " + trigger.count);
                if (caseMatch && trigger.count != null && globalOpenCount == trigger.count) {
                    logger.warning("DEBUG: global_open_count TRIGGERED!");
                    String msg = trigger.message.replace("{case}", caseName).replace("{count}", String.valueOf(globalOpenCount));
                    dbmApi.getMessageSender().sendWebhook(trigger.webhook, msg);
                }
            }
        }
    }
} 