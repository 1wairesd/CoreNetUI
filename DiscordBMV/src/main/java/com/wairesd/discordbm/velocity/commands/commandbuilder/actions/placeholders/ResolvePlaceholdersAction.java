package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.placeholders;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.commands.commandbuilder.utils.MessageFormatterUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResolvePlaceholdersAction implements CommandAction {
    private final String template;
    private final String playerTemplate;
    private final PlaceholdersResolver resolver;

    public ResolvePlaceholdersAction(Map<String, Object> properties, DiscordBMV plugin) {
        this.template = (String) properties.get("template");
        this.playerTemplate = (String) properties.get("player");
        this.resolver = new PlaceholdersResolver(plugin);
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();
        String playerName = MessageFormatterUtils.format(playerTemplate, event, context, false);
        return resolver.resolvePlaceholders(template, playerName, context);
    }
}
