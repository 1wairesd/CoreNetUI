package com.wairesd.discordbm.host.common.discord.response.handler.error.option;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.error.CommandErrorMessages;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.error.CommandErrorType;
import com.wairesd.discordbm.host.common.utils.Error;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Condition {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));

    public static void handleConditionError(SlashCommandInteractionEvent event, String errorMessage, boolean ephemeral) {
        try {
            String[] parts = errorMessage.split(":", 3);
            if (parts.length < 2) {
                event.getHook().sendMessage("Invalid error format").setEphemeral(ephemeral).queue();
                return;
            }

            String errorType = parts[1];
            Map<String, String> placeholders = new HashMap<>();

            if (parts.length > 2) {
                String[] placeholderPairs = parts[2].split(",");
                for (String pair : placeholderPairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        placeholders.put(keyValue[0], keyValue[1]);
                    }
                }
            }

            CommandErrorType commandErrorType = Error.parseErrorType(errorType);
            MessageEmbed embed = CommandErrorMessages.createErrorEmbed(commandErrorType, placeholders);

            event.getHook().sendMessageEmbeds(embed).setEphemeral(ephemeral).queue();

        } catch (Exception e) {
            logger.error("Error handling condition error: {}", e.getMessage(), e);
            event.getHook().sendMessage("Error processing condition").setEphemeral(ephemeral).queue();
        }
    }
}
