package com.wairesd.discordbm.velocity.commands.commandbuilder.listeners.modals;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModalInteractionListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ModalInteractionListener.class);

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalID = event.getModalId();
        if (!modalID.startsWith("form_")) return;

        try {
            Pair<CompletableFuture<Void>, Context> handler = DiscordBMV.plugin.getFormHandlers().get(modalID);
            if (handler == null) return;

            Map<String, String> responses = event.getValues().stream()
                    .collect(Collectors.toMap(
                            input -> input.getId(),
                            input -> input.getAsString()
                    ));

            Context context = handler.getRight();
            context.setFormResponses(responses);
            context.setHook(event.getHook());

            event.deferReply(false).queue();

            handler.getLeft().complete(null);

        } catch (Exception e) {
            logger.error("Modal Window Processing Error", e);
            event.reply("An error occurred while processing the form.").setEphemeral(true).queue();
        } finally {
            DiscordBMV.plugin.getFormHandlers().remove(modalID);
        }
    }
}