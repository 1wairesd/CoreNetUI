package com.wairesd.discordbm.host.common.commandbuilder.components.forms.action;

import com.wairesd.discordbm.host.common.commandbuilder.components.forms.builder.CommandFormBuilder;
import com.wairesd.discordbm.host.common.commandbuilder.components.forms.repository.FormRepository;
import com.wairesd.discordbm.host.common.commandbuilder.components.forms.sender.FormSender;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.config.configurators.Forms;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
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
    private final DiscordBMHPlatformManager platformManager;

    public SendFormAction(Map<String, Object> properties, DiscordBMHPlatformManager platformManager) {
        this.formName = (String) properties.get("form_name");
        this.platformManager = platformManager;
        if (formName == null || formName.isEmpty()) {
            throw new IllegalArgumentException("form_name is required for send_form action");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        Forms.FormStructured form = formRepository.getForm(formName);
        String modalId = "form_" + UUID.randomUUID();
        CompletableFuture<Void> future = new CompletableFuture<>();

        platformManager.getFormHandlers().put(modalId, new ImmutablePair<>(future, context));
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
