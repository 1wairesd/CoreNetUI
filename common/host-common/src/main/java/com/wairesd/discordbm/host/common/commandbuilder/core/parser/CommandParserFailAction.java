package com.wairesd.discordbm.host.common.commandbuilder.core.parser;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandParserFailAction {

    public static List<CommandAction> parse(Map<String, Object> cmdData, DiscordBMHPlatformManager platformManager) {
        Object raw = cmdData.get("fail-actions");
        if (!(raw instanceof List<?> list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .filter(item -> item instanceof Map)
                .map(item -> (Map<String, Object>) item)
                .map(actionMap -> CommandParserAction.parseAction(actionMap, platformManager))
                .collect(Collectors.toList());
    }
}
