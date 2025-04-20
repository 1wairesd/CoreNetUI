package com.wairesd.discordbm.velocity.command.custom;

import com.wairesd.discordbm.velocity.command.custom.models.Context;
import com.wairesd.discordbm.velocity.command.custom.models.CustomCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    public void execute(SlashCommandInteractionEvent event, CustomCommand command) {
        if (event == null || command == null) {
            throw new IllegalArgumentException("Event and command cannot be null");
        }

        event.deferReply().queue(hook -> {
            Context context = new Context(event);

            if (!command.getConditions().stream().allMatch(condition -> condition.check(context))) {
                hook.sendMessage("You don't meet the conditions to use this command.")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            command.getActions().forEach(action -> action.execute(context));
            String messageText = context.getMessageText().isEmpty() ? " " : context.getMessageText();

            var messageAction = hook.sendMessage(messageText);
            if (!context.getButtons().isEmpty()) {
                messageAction.addActionRow(context.getButtons());
            }
            messageAction.queue();
        });
    }

}
