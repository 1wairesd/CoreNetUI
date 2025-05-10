package com.wairesd.discordbm.velocity.commands.commandbuilder.listeners.buttons;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons.ButtonActionRegistry;
import com.wairesd.discordbm.velocity.config.configurators.Forms;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ButtonInteractionListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ButtonInteractionListener.class);

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        logger.debug("Button clicked: {}", buttonId);

        ButtonActionRegistry.FormButtonData formData = ButtonActionRegistry.getFormButtonData(buttonId);
        if (formData != null) {
            String requiredRoleId = formData.getRequiredRoleId();
            if (requiredRoleId != null && !event.getMember().getRoles().stream().anyMatch(role -> role.getId().equals(requiredRoleId))) {
                event.reply("You do not have permission to use this button.").setEphemeral(true).queue();
                return;
            }

            String formName = formData.getFormName();
            String messageTemplate = formData.getMessageTemplate();
            Forms.FormStructured form = Forms.getForms().get(formName);

            if (form == null) {
                event.deferReply(true).queue(hook ->
                        hook.sendMessage("No form found.").setEphemeral(true).queue());
                return;
            }

            String modalID = "form_" + UUID.randomUUID();
            Modal.Builder modalBuilder = Modal.create(modalID, form.title());

            for (Forms.FormStructured.Field field : form.fields()) {
                TextInput input = TextInput.create(
                                field.variable(),
                                field.label(),
                                TextInputStyle.valueOf(field.type().toUpperCase())
                        ).setPlaceholder(field.placeholder())
                        .setRequired(field.required())
                        .build();
                modalBuilder.addActionRow(input);
            }

            Modal modal = modalBuilder.build();
            DiscordBMV.plugin.getFormHandlers().put(modalID, messageTemplate);
            event.replyModal(modal).queue();
            return;
        }

        String message = ButtonActionRegistry.getMessage(buttonId);
        event.deferReply(true).queue(hook -> {
            if (message == null) {
                logger.warn("Button {} not found or expired", buttonId);
                hook.sendMessage("The action has expired or is invalid").setEphemeral(true).queue();
            } else {
                hook.sendMessage(message).setEphemeral(true).queue();
            }
        });
    }
}