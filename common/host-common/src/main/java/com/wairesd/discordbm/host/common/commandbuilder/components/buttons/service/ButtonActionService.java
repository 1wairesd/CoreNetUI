package com.wairesd.discordbm.host.common.commandbuilder.components.buttons.service;

import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.model.FormButtonData;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.registry.ButtonActionRegistry;

public class ButtonActionService {
    private final ButtonActionRegistry registry = new ButtonActionRegistry();

    public FormButtonData getFormButtonData(String buttonId) {
        return registry.getFormButtonData(buttonId);
    }

    public String getMessage(String buttonId) {
        return registry.getMessage(buttonId);
    }
}
