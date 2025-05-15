package com.wairesd.discordbm.velocity.commands.commandbuilder.checker;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class PermissionChecker {
    public boolean hasPermission(ButtonInteractionEvent event, String requiredRoleId) {
        if (requiredRoleId == null) return true;
        return event.getMember().getRoles().stream()
                .anyMatch(role -> role.getId().equals(requiredRoleId));
    }
}
