package com.wairesd.discordbm.host.common.commandbuilder.components.modal.repository;

import com.wairesd.discordbm.host.common.config.configurators.Forms;

public class ModalRepository {
    public Forms.FormStructured getForm(String formName) {
        Forms.FormStructured form = Forms.getForms().get(formName);
        if (form == null) {
            throw new IllegalArgumentException("Form not found: " + formName);
        }
        return form;
    }
} 