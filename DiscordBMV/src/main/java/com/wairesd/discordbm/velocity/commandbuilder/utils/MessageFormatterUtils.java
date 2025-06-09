package com.wairesd.discordbm.velocity.commandbuilder.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.velocity.commandbuilder.models.placeholders.PlaceholdersChannel;
import com.wairesd.discordbm.velocity.commandbuilder.models.placeholders.PlaceholdersResolved;
import com.wairesd.discordbm.velocity.commandbuilder.models.placeholders.PlaceholdersServer;
import com.wairesd.discordbm.velocity.commandbuilder.models.placeholders.PlaceholdersUser;
import com.wairesd.discordbm.velocity.commandbuilder.models.context.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class MessageFormatterUtils {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private static final PlaceholderManager placeholderManager = new PlaceholderManager();

    static {
        placeholderManager.registerPlaceholder(new PlaceholdersUser());
        placeholderManager.registerPlaceholder(new PlaceholdersServer());
        placeholderManager.registerPlaceholder(new PlaceholdersChannel());
        placeholderManager.registerPlaceholder(new PlaceholdersResolved());
    }

    public static CompletableFuture<String> format(@Nullable String template, Interaction event, Context context, boolean debugLog) {
        if (template == null) return CompletableFuture.completedFuture("");

        String result = template;

        if (event instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent slashEvent = (SlashCommandInteractionEvent) event;
            for (OptionMapping option : slashEvent.getOptions()) {
                result = result.replace("{" + option.getName() + "}", option.getAsString());
            }
        }

        if (context.getVariables() != null) {
            for (Map.Entry<String, String> entry : context.getVariables().entrySet()) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        
        result = context.replacePlaceholders(result);
        
        if (context.getResolvedPlaceholders() != null && !context.getResolvedPlaceholders().isEmpty()) {
            for (Map.Entry<String, String> entry : context.getResolvedPlaceholders().entrySet()) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }

        if (debugLog) logger.info("Formatted message: {}", result);

        return placeholderManager.applyPlaceholders(result, event, context);
    }
}