package com.wairesd.discordbm.velocity.command.custom.listeners;

import com.wairesd.discordbm.velocity.command.custom.actions.ButtonActionRegistry;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonInteractionListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();
        String msg = ButtonActionRegistry.getMessage(id);

        event.deferReply(true).queue(hook -> {
            if (msg != null) {
                hook.sendMessage(msg).setEphemeral(true).queue();
            } else {
                hook.sendMessage("Unknown button or valid expired").setEphemeral(true).queue();
            }
        });
    }
}