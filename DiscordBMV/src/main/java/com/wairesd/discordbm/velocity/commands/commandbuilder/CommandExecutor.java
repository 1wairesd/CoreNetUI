package com.wairesd.discordbm.velocity.commands.commandbuilder;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.structures.CommandStructured;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    public void execute(SlashCommandInteractionEvent event, CommandStructured command) {
        boolean ephemeral = command.getEphemeral() != null ?
                command.getEphemeral() :
                Settings.isDefaultEphemeral();

        event.deferReply(ephemeral).queue(hook -> {

        if (event == null || command == null) {
            throw new IllegalArgumentException("Event and command cannot be null");
        }
            Context context = new Context(event);

            if (!command.getConditions().stream().allMatch(condition -> condition.check(context))) {
                hook.sendMessage("You don't meet the conditions...")
                        .setEphemeral(ephemeral)
                        .queue();
                return;
            }

            CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
            for (CommandAction action : command.getActions()) {
                chain = chain.thenCompose(voidResult -> action.execute(context));
            }

            chain.thenRun(() -> {
                switch (context.getResponseType()) {
                    case REPLY:
                        sendReply(hook, context);
                        break;
                    case SPECIFIC_CHANNEL:
                        sendToChannel(event.getJDA(), context);
                        break;
                    case DIRECT_MESSAGE:
                        sendDirectMessage(context);
                        hook.deleteOriginal().queue();
                        break;
                    case EDIT_MESSAGE:
                        editMessage(event.getChannel(), context);
                        hook.deleteOriginal().queue();
                        break;
                }
            }).exceptionally(ex -> {
                logger.error("Error when executing command actions: {}", ex.getMessage(), ex);
                hook.sendMessage("An error occurred while executing the command.").setEphemeral(true).queue();
                return null;
            });
        });
    }

    private void sendReply(InteractionHook hook, Context context) {
        String messageText = context.getMessageText();
        if (messageText == null || messageText.trim().isEmpty()) {
            hook.sendMessage("Message content not provided.").setEphemeral(true).queue();
        } else {
            var messageAction = hook.sendMessage(messageText);
            if (context.getEmbed() != null) {
                messageAction.setEmbeds(context.getEmbed());
            }
            if (!context.getButtons().isEmpty()) {
                messageAction.addActionRow(context.getButtons());
            }
            messageAction.queue();
        }
    }

    private void sendToChannel(JDA jda, Context context) {
        TextChannel channel = jda.getTextChannelById(context.getTargetChannelId());
        if (channel != null) {
            var messageAction = channel.sendMessage(context.getMessageText());
            if (context.getEmbed() != null) {
                messageAction.setEmbeds(context.getEmbed());
            }
            if (!context.getButtons().isEmpty()) {
                messageAction.addActionRow(context.getButtons());
            }
            messageAction.queue();
        } else {
            logger.warn("Target channel not found for ID: {}", context.getTargetChannelId());
        }
    }

    private void sendDirectMessage(Context context) {
        String userId = context.getTargetUserId();
        if (userId == null || userId.isEmpty()) {
            logger.debug("Attempt to send DM with empty user ID");
            logger.warn("Direct message failed - no target user specified");
            return;
        }

        if (Settings.isDebugCommandRegistrations()) {
            logger.debug("Sending DM to user {}: {}", userId, context.getMessageText());
        }

        User user = context.getEvent().getJDA().getUserById(userId);
        if (user != null) {
            user.openPrivateChannel().queue(pc -> {
                var messageAction = pc.sendMessage(context.getMessageText());
                if (context.getEmbed() != null) {
                    messageAction.setEmbeds(context.getEmbed());
                }
                if (!context.getButtons().isEmpty()) {
                    messageAction.addActionRow(context.getButtons());
                }
                messageAction.queue();
            });
        } else {
            logger.warn("Target user not found for ID: {}", userId);
        }
    }

    private void editMessage(MessageChannelUnion channel, Context context) {
        List<LayoutComponent> components = context.getButtons().isEmpty()
                ? null
                : Collections.singletonList(ActionRow.of(context.getButtons()));

        channel.editMessageById(context.getMessageIdToEdit(), context.getMessageText())
                .setComponents(components)
                .setEmbeds(context.getEmbed() != null ? List.of(context.getEmbed()) : Collections.emptyList())
                .queue();
    }
}
