package com.wairesd.discordbm.velocity.commandbuilder.buttons.service;

import com.wairesd.discordbm.velocity.commandbuilder.buttons.model.FormButtonData;
import com.wairesd.discordbm.velocity.commandbuilder.buttons.registry.ButtonActionRegistry;

public class ButtonActionService {
    private final ButtonActionRegistry registry = new ButtonActionRegistry();

    public FormButtonData getFormButtonData(String buttonId) {
        return registry.getFormButtonData(buttonId);
    }

    public String getMessage(String buttonId) {
        return registry.getMessage(buttonId);
    }
}
