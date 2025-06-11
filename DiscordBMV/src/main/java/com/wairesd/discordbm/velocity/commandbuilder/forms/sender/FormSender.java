package com.wairesd.discordbm.velocity.commandbuilder.forms.sender;

import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.concurrent.CompletableFuture;

public class FormSender {
    public void send(SlashCommandInteractionEvent event, Modal modal, Context context, CompletableFuture<Void> future) {
        try {
            event.replyModal(modal).queue(
                    success -> {},
                    error -> {
                        context.getHook().sendMessage("The form could not be opened. Try again.")
                                .setEphemeral(true)
                                .queue();
                        future.completeExceptionally(error);
                    }
            );
        } catch (IllegalStateException e) {
            context.getHook().sendMessage("The form could not be opened. Try again.")
                    .setEphemeral(true)
                    .queue();
            future.completeExceptionally(e);
        }
    }
} 