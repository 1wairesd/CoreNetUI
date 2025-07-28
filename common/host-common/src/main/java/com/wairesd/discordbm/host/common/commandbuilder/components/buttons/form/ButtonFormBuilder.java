package com.wairesd.discordbm.host.common.commandbuilder.components.buttons.form;

import com.wairesd.discordbm.host.common.commandbuilder.components.modal.builder.BaseModalBuilder;
import com.wairesd.discordbm.host.common.config.configurators.Forms;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.UUID;

public class ButtonFormBuilder extends BaseModalBuilder {
    public Modal buildModal(Forms.FormStructured form) {
        String modalId = "form_" + UUID.randomUUID();
        return createBaseModal(modalId, form).build();
    }
} 