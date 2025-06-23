package com.wairesd.discordbm.host.common.commandbuilder.security.conditions.chance;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.conditions.CommandCondition;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;

import java.util.Map;
import java.util.Random;

public class ChanceCondition implements CommandCondition {
    private final int percent;
    private final Random random = new Random();

    public ChanceCondition(Map<String, Object> properties) {
        Object percentObj = properties.get("percent");
        if (percentObj == null) {
            throw new IllegalArgumentException("Percent is required for ChanceCondition");
        }
        if (percentObj instanceof Number) {
            this.percent = ((Number) percentObj).intValue();
        } else {
            try {
                this.percent = (int) Double.parseDouble(percentObj.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid percent value: " + percentObj);
            }
        }
        if (this.percent < 0 || this.percent > 100) {
            throw new IllegalArgumentException("Percent must be between 0 and 100");
        }
    }

    @Override
    public boolean check(Context context) {
        return random.nextInt(100) < percent;
    }
}