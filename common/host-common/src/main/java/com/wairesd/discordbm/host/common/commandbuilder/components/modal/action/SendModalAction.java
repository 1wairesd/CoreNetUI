package com.wairesd.discordbm.host.common.commandbuilder.components.modal.action;

import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.commandbuilder.components.modal.builder.CommandModalBuilder;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.components.modal.repository.ModalRepository;
import com.wairesd.discordbm.host.common.commandbuilder.components.modal.sender.ModalSender;
import com.wairesd.discordbm.host.common.config.configurators.Forms;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SendModalAction implements CommandAction {
    private final String formName;
    private final ModalRepository formRepository = new ModalRepository();
    private final CommandModalBuilder formBuilder = new CommandModalBuilder();
    private final ModalSender modalSender = new ModalSender();
    private final DiscordBMHPlatformManager platformManager;

    public SendModalAction(Map<String, Object> properties, DiscordBMHPlatformManager platformManager) {
        this.formName = (String) properties.get("modal_name");
        this.platformManager = platformManager;
        if (formName == null || formName.isEmpty()) {
            throw new IllegalArgumentException("modal_name is required for send_modal action");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        Forms.FormStructured form = formRepository.getForm(formName);
        String modalId = "modal_" + UUID.randomUUID();
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
