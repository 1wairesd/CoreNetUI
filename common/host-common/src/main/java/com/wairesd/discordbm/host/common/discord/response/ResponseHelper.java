package com.wairesd.discordbm.host.common.discord.response;

import com.wairesd.discordbm.host.common.config.configurators.Messages;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class ResponseHelper {

    public void replyCommandRestrictedToDM(SlashCommandInteractionEvent event) {
        event.reply("This command is only available in direct messages.")
                .setEphemeral(true)
                .queue();
    }

    public void replySelectionTimeout(StringSelectInteractionEvent event) {
        event.reply(Messages.get(Messages.Keys.SERVER_SELECTION_TIMEOUT))
                .setEphemeral(true)
                .queue();
    }

    public void replyNoServerSelected(StringSelectInteractionEvent event) {
        event.reply(Messages.get(Messages.Keys.SERVER_SELECTION_NO_SERVER))
                .setEphemeral(true)
                .queue();
    }

    public void replyServerNotFound(StringSelectInteractionEvent event) {
        event.reply(Messages.get(Messages.Keys.SERVER_SELECTION_NOT_FOUND))
                .setEphemeral(true)
                .queue();
    }
}
