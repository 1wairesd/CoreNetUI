package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.buttons.FormButtonData;

public class ButtonActionService {
    private final ButtonActionRegistry registry = new ButtonActionRegistry();

    public FormButtonData getFormButtonData(String buttonId) {
        return registry.getFormButtonData(buttonId);
    }

    public String getMessage(String buttonId) {
        return registry.getMessage(buttonId);
    }
}
