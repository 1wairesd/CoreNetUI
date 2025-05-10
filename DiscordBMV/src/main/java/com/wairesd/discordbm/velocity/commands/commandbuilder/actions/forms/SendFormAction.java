package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.forms;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.config.configurators.Forms;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SendFormAction implements CommandAction {
    private final String formName;

    public SendFormAction(Map<String, Object> properties) {
        this.formName = (String) properties.get("form_name");
        if (formName == null || formName.isEmpty()) {
            throw new IllegalArgumentException("form_name is required for send_form action");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        Forms.FormStructured form = Forms.getForms().get(formName);
        if (form == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Form not found: " + formName));
        }

        String uniqueID = UUID.randomUUID().toString();
        String modalID = "form_" + uniqueID;
        CompletableFuture<Void> completionFuture = new CompletableFuture<>();
        DiscordBMV.plugin.getFormHandlers().put(modalID, new ImmutablePair<>(completionFuture, context));

        Modal.Builder modalBuilder = Modal.create(modalID, form.title());
        for (Forms.FormStructured.Field field : form.fields()) {
            TextInput.Builder inputBuilder = TextInput.create(field.variable(), field.label(), TextInputStyle.valueOf(field.type().toUpperCase()))
                    .setPlaceholder(field.placeholder())
                    .setRequired(field.required());
            modalBuilder.addActionRow(inputBuilder.build());
        }
        Modal modal = modalBuilder.build();

        if (context.getEvent() instanceof SlashCommandInteractionEvent event) {
            try {
                event.replyModal(modal).queue(
                        success -> {},
                        error -> {
                            context.getHook().sendMessage("The form could not be opened. Try again.")
                                    .setEphemeral(true)
                                    .queue();
                            completionFuture.completeExceptionally(error);
                        }
                );
            } catch (IllegalStateException e) {
                context.getHook().sendMessage("The form could not be opened. Try again.")
                        .setEphemeral(true)
                        .queue();
                completionFuture.completeExceptionally(e);
            }
        } else {
            completionFuture.completeExceptionally(
                    new IllegalStateException("This interaction does not support modal replies.")
            );
        }
        return completionFuture;
    }
}