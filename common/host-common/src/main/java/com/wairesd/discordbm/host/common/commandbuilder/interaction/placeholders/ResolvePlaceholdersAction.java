package com.wairesd.discordbm.host.common.commandbuilder.interaction.placeholders;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders.PlaceholdersDiscordBM;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResolvePlaceholdersAction implements CommandAction {
    private final String template;
    private final String playerTemplate;
    private final DiscordBMHPlatformManager platformManager;

    public ResolvePlaceholdersAction(Map<String, Object> properties, DiscordBMHPlatformManager platformManager) {
        this.template = (String) properties.get("template");
        this.playerTemplate = (String) properties.get("player");
        this.platformManager = platformManager;
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        SlashCommandInteractionEvent event = (SlashCommandInteractionEvent) context.getEvent();
        return MessageFormatterUtils.format(playerTemplate, event, context, false)
                .thenCompose(playerName -> {
                    PlaceholdersResolver resolver = new PlaceholdersResolver(platformManager);
                    
                    if (context.getVariables() != null && 
                        context.getVariables().containsKey(PlaceholdersDiscordBM.SERVER_NAME_VAR)) {
                        return resolver.resolvePlaceholders(template, playerName, context);
                    }
                    
                    var proxy = platformManager.getVelocityProxy();
                    var playerOpt = proxy.getPlayer(playerName);
                    if (playerOpt.isPresent()) {
                        var player = playerOpt.get();
                        var serverOpt = player.getCurrentServer();
                        if (serverOpt.isPresent()) {
                            String serverName = serverOpt.get().getServerInfo().getName();
                            if (context.getVariables() == null) {
                                Map<String, String> variables = new HashMap<>();
                                variables.put(PlaceholdersDiscordBM.SERVER_NAME_VAR, serverName);
                                context.setVariables(variables);
                            } else {
                                context.getVariables().put(PlaceholdersDiscordBM.SERVER_NAME_VAR, serverName);
                            }
                        }
                    }
                    
                    return resolver.resolvePlaceholders(template, playerName, context);
                });
    }
}