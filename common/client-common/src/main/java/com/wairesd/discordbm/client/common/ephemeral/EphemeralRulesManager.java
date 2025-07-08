package com.wairesd.discordbm.client.common.ephemeral;

import com.wairesd.discordbm.client.common.platform.Platform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EphemeralRulesManager {
    private final Platform platform;
    private final Map<String, Boolean> ephemeralRulesRegistry = Collections.synchronizedMap(new HashMap<>());
    private volatile boolean ephemeralRulesDirty = false;

    public EphemeralRulesManager(Platform platform) {
        this.platform = platform;
    }

    public void registerEphemeralRules(Map<String, Boolean> rules) {
        synchronized (ephemeralRulesRegistry) {
            ephemeralRulesRegistry.putAll(rules);
        }
        ephemeralRulesDirty = true;
    }

    private void sendEphemeralRulesIfConnected(Map<String, Boolean> rules) {
        if (platform.isConnected()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "ephemeral_rules");
            msg.put("rules", rules);
            String json = new com.google.gson.Gson().toJson(msg);
            platform.getNettyService().sendNettyMessage(json);
        }
    }

    public void resendAllEphemeralRules() {
        if (platform.isConnected()) {
            Map<String, Boolean> snapshot;
            synchronized (ephemeralRulesRegistry) {
                snapshot = new HashMap<>(ephemeralRulesRegistry);
            }
            if (!snapshot.isEmpty() && ephemeralRulesDirty) {
                sendEphemeralRulesIfConnected(snapshot);
                ephemeralRulesDirty = false;
            }
        }
    }
} 