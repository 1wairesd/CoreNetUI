package com.wairesd.discordbm.velocity.commands.commandbuilder.listeners.buttons;

import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons.ButtonActionRegistry;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonInteractionListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ButtonInteractionListener.class);

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        logger.debug("Button clicked: {}", buttonId);

        String message = ButtonActionRegistry.getMessage(buttonId);

        event.deferReply(true).queue(hook -> {
            if (message == null) {
                logger.warn("Button {} not found or expired", buttonId);
                hook.sendMessage("Action expired or invalid").setEphemeral(true).queue();
            } else {
                hook.sendMessage(message).setEphemeral(true).queue();
            }
        });
    }
}
