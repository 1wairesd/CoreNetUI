package com.wairesd.discordbm.host.common.commandbuilder.components.modal.builder;

import com.wairesd.discordbm.host.common.config.configurators.Forms;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public abstract class BaseModalBuilder {
    protected Modal.Builder createBaseModal(String modalId, Forms.FormStructured form) {
        Modal.Builder modalBuilder = Modal.create(modalId, form.title());
        for (Forms.FormStructured.Field field : form.fields()) {
            modalBuilder.addActionRow(createTextInput(field));
        }
        return modalBuilder;
    }

    protected TextInput createTextInput(Forms.FormStructured.Field field) {
        return TextInput.create(
                field.variable(),
                field.label(),
                TextInputStyle.valueOf(field.type().toUpperCase()))
                .setPlaceholder(field.placeholder())
                .setRequired(field.required())
                .build();
    }
} 