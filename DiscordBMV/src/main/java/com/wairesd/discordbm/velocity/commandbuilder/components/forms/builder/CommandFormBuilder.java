package com.wairesd.discordbm.velocity.commandbuilder.components.forms.builder;

import com.wairesd.discordbm.velocity.config.configurators.Forms;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class CommandFormBuilder extends BaseFormBuilder {
    public Modal build(String modalId, Forms.FormStructured form) {
        return createBaseModal(modalId, form).build();
    }
} 