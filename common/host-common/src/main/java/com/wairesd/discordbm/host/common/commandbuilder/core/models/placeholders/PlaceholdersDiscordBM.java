package com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.concurrent.CompletableFuture;

public class PlaceholdersDiscordBM implements Placeholder {

    public static final String SERVER_NAME_VAR = "discordbm_server_name";
    
    @Override
    public CompletableFuture<String> replace(String template, Interaction event, Context context) {
        if (!template.contains("{DiscordBM_server_name}")) {
            return CompletableFuture.completedFuture(template);
        }
        
        String serverName = "Unknown";
        if (context != null && context.getVariables() != null && context.getVariables().containsKey(SERVER_NAME_VAR)) {
            serverName = context.getVariables().get(SERVER_NAME_VAR);
        }
        
        String result = template.replace("{DiscordBM_server_name}", serverName);
        return CompletableFuture.completedFuture(result);
    }
} 