package com.wairesd.discordbm.api.command;

import java.util.Map;

public class ChanceCondition implements CommandCondition {
    private final int percent;
    public ChanceCondition(int percent) {
        if (percent < 0 || percent > 100) throw new IllegalArgumentException("Percent must be 0-100");
        this.percent = percent;
    }
    @Override
    public String getType() { return "chance"; }
    @Override
    public Map<String, Object> serialize() {
        return Map.of("type", "chance", "percent", percent);
    }
    public int getPercent() { return percent; }
} 