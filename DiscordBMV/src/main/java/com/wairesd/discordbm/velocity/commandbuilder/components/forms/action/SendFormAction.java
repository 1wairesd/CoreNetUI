package com.wairesd.discordbm.velocity.commandbuilder.components.forms.action;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commandbuilder.components.forms.builder.CommandFormBuilder;
import com.wairesd.discordbm.velocity.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.velocity.commandbuilder.components.forms.repository.FormRepository;
import com.wairesd.discordbm.velocity.commandbuilder.components.forms.sender.FormSender;
import com.wairesd.discordbm.velocity.config.configurators.Forms;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SendFormAction implements CommandAction {
    private final String formName;
    private final FormRepository formRepository = new FormRepository();
    private final CommandFormBuilder formBuilder = new CommandFormBuilder();
    private final FormSender modalSender = new FormSender();

    public SendFormAction(Map<String, Object> properties) {
        this.formName = (String) properties.get("form_name");
        if (formName == null || formName.isEmpty()) {
            throw new IllegalArgumentException("form_name is required for send_form action");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        Forms.FormStructured form = formRepository.getForm(formName);
        String modalId = "form_" + UUID.randomUUID();
        CompletableFuture<Void> future = new CompletableFuture<>();

        DiscordBMV.plugin.getFormHandlers().put(modalId, new ImmutablePair<>(future, context));
        Modal modal = formBuilder.build(modalId, form);

        if (context.getEvent() instanceof SlashCommandInteractionEvent event) {
            modalSender.send(event, modal, context, future);
        } else {
            future.completeExceptionally(
                    new IllegalStateException("This interaction does not support modal replies.")
            );
        }

        return future;
    }
}
