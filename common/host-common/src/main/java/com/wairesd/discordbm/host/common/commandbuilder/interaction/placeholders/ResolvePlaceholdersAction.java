package com.wairesd.discordbm.host.common.commandbuilder.interaction.placeholders;

import com.wairesd.discordbm.host.common.DiscordBMVPlatform;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResolvePlaceholdersAction implements CommandAction {
    private final String template;
    private final String playerTemplate;
    private final DiscordBMVPlatform discordHost;

    public ResolvePlaceholdersAction(Map<String, Object> properties, DiscordBMVPlatform discordHost) {
        this.template = (String) properties.get("template");
        this.playerTemplate = (String) properties.get("player");
        this.discordHost = discordHost;
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();
        return MessageFormatterUtils.format(playerTemplate, event, context, false)
                .thenCompose(playerName -> {
                    PlaceholdersResolver resolver = new PlaceholdersResolver(discordHost);
                    return resolver.resolvePlaceholders(template, playerName, context);
                });
    }
}