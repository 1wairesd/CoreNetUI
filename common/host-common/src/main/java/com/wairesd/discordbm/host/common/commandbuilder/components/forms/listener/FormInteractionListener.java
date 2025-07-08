package com.wairesd.discordbm.host.common.commandbuilder.components.forms.listener;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FormInteractionListener extends ListenerAdapter {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final DiscordBMHPlatformManager platformManager;

    public FormInteractionListener(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalID = event.getModalId();
        if (!modalID.startsWith("form_")) return;

        try {
            Object handler = platformManager.getFormHandlers().get(modalID);
            if (handler == null) return;

            Map<String, String> responses = event.getValues().stream()
                    .collect(Collectors.toMap(
                            input -> input.getId(),
                            input -> input.getAsString()
                    ));

            if (handler instanceof Pair) {
                Pair<CompletableFuture<Void>, Context> pair = (Pair<CompletableFuture<Void>, Context>) handler;
                Context context = pair.getRight();
                context.setFormResponses(responses);
                context.setHook(event.getHook());
                event.deferReply(false).queue();
                pair.getLeft().complete(null);
            } else if (handler instanceof String) {
                String messageTemplate = (String) handler;
                String messageWithFormPlaceholders = replacePlaceholders(messageTemplate, responses);
                Context context = new Context(event);
                MessageFormatterUtils.format(messageWithFormPlaceholders, event, context, false)
                        .thenAccept(formatted -> event.reply(formatted).setEphemeral(true).queue());
            }
        } catch (Exception e) {
            logger.error("Modal Window Processing Error", e);
            event.reply("An error occurred while processing the form.").setEphemeral(true).queue();
        } finally {
            platformManager.getFormHandlers().remove(modalID);
        }
    }

    private String replacePlaceholders(String template, Map<String, String> responses) {
        for (Map.Entry<String, String> entry : responses.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}