package com.wairesd.discordbm.host.common.commandbuilder.commands.processor;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.CommandResponder;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.messages.SendMessageAction;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class CommandProcessor {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));

    public void process(CommandStructured command, Context context, SlashCommandInteractionEvent event, CommandResponder responder) {
        boolean ephemeral = command.getEphemeral() != null ? command.getEphemeral() : Settings.isDefaultEphemeral();

        boolean hasCustomReply = false;
        for (CommandAction action : command.getActions()) {
            if (action instanceof SendMessageAction sendMsg && sendMsg.getReplyMessageId() != null && !sendMsg.getReplyMessageId().isEmpty()) {
                hasCustomReply = true;
                break;
            }
        }

        if (command.hasFormAction()) {
            executeActions(command.getActions(), context)
                    .thenRun(() -> {
                        responder.respond(context, event);
                    })
                    .exceptionally(ex -> {
                        event.reply("An error occurred while executing the command.").setEphemeral(true).queue();
                        return null;
                    });
        } else if (hasCustomReply) {
            executeActions(command.getActions(), context)
                .exceptionally(ex -> {
                    event.reply("An error occurred while executing the command.").setEphemeral(true).queue();
                    return null;
                });
        } else {
            event.deferReply(ephemeral).queue(hook -> {
                context.setHook(hook);
                executeActions(command.getActions(), context)
                        .thenRun(() -> {
                            responder.respondAndCleanup(context, event, hook);
                        })
                        .exceptionally(ex -> {
                            hook.sendMessage("An error occurred while executing the command.").setEphemeral(true).queue();
                            return null;
                        });
            });
        }
    }

    private CompletableFuture<Void> executeActions(Iterable<CommandAction> actions, Context context) {
        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
        for (CommandAction action : actions) {
            chain = chain.thenCompose(v -> action.execute(context));
        }
        return chain;
    }
}
