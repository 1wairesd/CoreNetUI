package com.wairesd.discordbm.host.common.discord.response.handler.error;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.response.handler.error.option.Condition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.LoggerFactory;

public class ErrorHandler {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));

    public static void handleConditionError(SlashCommandInteractionEvent event, String errorMessage, boolean ephemeral) {
        Condition.handleConditionError(event, errorMessage, ephemeral);
    }
}
