package com.wairesd.discordbm.host.common.commandbuilder.interaction.placeholders;

import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders.PlaceholdersDiscordBM;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;

public class ResolvePlaceholdersAction implements CommandAction {

    private final String template;
    private final String playerTemplate;
    private final DiscordBMHPlatformManager platformManager;

    public ResolvePlaceholdersAction(Map<String, Object> properties,
                                     DiscordBMHPlatformManager platformManager) {
        this.template = (String) properties.get("template");
        this.playerTemplate = (String) properties.get("player");
        this.platformManager = platformManager;
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();
        return MessageFormatterUtils.format(playerTemplate, event, context, false)
                .thenCompose(playerName -> resolveWithPlayerName(playerName, context));
    }

    private CompletableFuture<Void> resolveWithPlayerName(String playerName, Context context) {
        PlaceholdersResolver resolver = new PlaceholdersResolver(platformManager);

        if (hasServerNameVariable(context)) {
            return resolver.resolvePlaceholders(template, playerName, context);
        }

        resolveServerNameFromPlayer(playerName, context);
        return resolver.resolvePlaceholders(template, playerName, context);
    }

    private boolean hasServerNameVariable(Context context) {
        return context.getVariables() != null &&
                context.getVariables().containsKey(PlaceholdersDiscordBM.SERVER_NAME_VAR);
    }

    private void resolveServerNameFromPlayer(String playerName, Context context) {
        var proxy = platformManager.getVelocityProxy();
        var playerOpt = proxy.getPlayer(playerName);

        playerOpt.ifPresent(player -> player.getCurrentServer().ifPresent(server -> {
            String serverName = server.getServerInfo().getName();
            Map<String, String> variables = context.getVariables();
            if (variables == null) {
                variables = new HashMap<>();
                context.setVariables(variables);
            }
            variables.put(PlaceholdersDiscordBM.SERVER_NAME_VAR, serverName);
        }));
    }
}
