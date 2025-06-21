package com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.concurrent.CompletableFuture;

public class PlaceholdersOption implements Placeholder {
    
    @Override
    public CompletableFuture<String> replace(String template, Interaction event, Context context) {
        if (!(event instanceof SlashCommandInteractionEvent)) {
            return CompletableFuture.completedFuture(template);
        }
        
        SlashCommandInteractionEvent slashEvent = (SlashCommandInteractionEvent) event;
        String result = template;

        int startIndex = result.indexOf("{option:");
        while (startIndex != -1) {
            int endIndex = result.indexOf("}", startIndex);
            if (endIndex == -1) {
                break;
            }
            
            String placeholder = result.substring(startIndex, endIndex + 1);
            String optionName = result.substring(startIndex + 8, endIndex);
            
            OptionMapping option = slashEvent.getOption(optionName);
            if (option != null) {
                String replacement = "";
                try {
                    replacement = option.getAsString();
                } catch (Exception e) {
                    try {
                        replacement = option.getAsUser().getAsMention();
                    } catch (Exception e2) {
                        try {
                            replacement = option.getAsChannel().getName();
                        } catch (Exception e3) {
                            replacement = optionName;
                        }
                    }
                }
                result = result.replace(placeholder, replacement);
            }
            
            startIndex = result.indexOf("{option:", startIndex + 1);
        }
        
        return CompletableFuture.completedFuture(result);
    }
    
    public static String resolveOption(String template, SlashCommandInteractionEvent event) {
        if (template == null) return null;
        
        // Обработка простого случая {channel}
        if (template.equals("{channel}")) {
            OptionMapping channelOption = event.getOption("channel");
            if (channelOption != null) {
                try {
                    return channelOption.getAsChannel().getId();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        
        return template;
    }
} 