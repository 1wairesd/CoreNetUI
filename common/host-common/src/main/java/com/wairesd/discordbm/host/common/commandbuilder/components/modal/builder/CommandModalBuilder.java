package com.wairesd.discordbm.host.common.commandbuilder.components.modal.builder;

import com.wairesd.discordbm.host.common.config.configurators.Forms;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class CommandModalBuilder extends BaseModalBuilder {
    public Modal build(String modalId, Forms.FormStructured form) {
        return createBaseModal(modalId, form).build();
    }
} 