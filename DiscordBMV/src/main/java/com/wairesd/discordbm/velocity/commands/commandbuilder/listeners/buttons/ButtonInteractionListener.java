package com.wairesd.discordbm.velocity.commands.commandbuilder.listeners.buttons;

import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons.ButtonActionRegistry;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonInteractionListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        String message = ButtonActionRegistry.getMessage(buttonId);

        event.deferReply(true).queue(hook -> {
            String response = message != null ? message : "Unknown button or valid expired";
            hook.sendMessage(response).setEphemeral(true).queue();
        });
    }
}
