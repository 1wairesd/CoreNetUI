package com.wairesd.discordbm.velocity.commandbuilder.components.buttons.form;

import com.wairesd.discordbm.velocity.commandbuilder.components.forms.builder.BaseFormBuilder;
import com.wairesd.discordbm.velocity.config.configurators.Forms;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.UUID;

public class ButtonFormBuilder extends BaseFormBuilder {
    public Modal buildModal(Forms.FormStructured form) {
        String modalId = "form_" + UUID.randomUUID();
        return createBaseModal(modalId, form).build();
    }
} 