package com.wairesd.discordbm.host.common.commandbuilder.interaction.messages;

import com.wairesd.discordbm.host.common.commandbuilder.core.channel.ChannelFetcher;
import com.wairesd.discordbm.host.common.commandbuilder.utils.message.MessageDeleter;
import com.wairesd.discordbm.host.common.commandbuilder.utils.message.MessageReferenceResolver;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DeleteMessageAction implements CommandAction {
    private final String label;
    private final String channelId;
    private final String messageId;
    private final String responseMessage;
    private final boolean deleteAll;

    private final MessageReferenceResolver resolver = new MessageReferenceResolver();
    private final ChannelFetcher fetcher = new ChannelFetcher();
    private final MessageDeleter deleter = new MessageDeleter();

    public DeleteMessageAction(Map<String, Object> properties) {
        this.label = (String) properties.get("label");
        this.channelId = (String) properties.get("channel_id");
        this.messageId = (String) properties.get("message_id");
        this.responseMessage = (String) properties.get("response_message");
        this.deleteAll = properties.containsKey("delete_all") ? Boolean.TRUE.equals(properties.get("delete_all")) : true;
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        return CompletableFuture.runAsync(() -> {
            if (label != null) {
                String fullLabel = (context.getEvent().getGuild() != null ? context.getEvent().getGuild().getId() : "DM") + "_" + label;
                if (deleteAll) {
                    List<String[]> refs = Commands.getPlatform().getAllMessageReferencesByLabel(fullLabel);
                    if (refs == null || refs.isEmpty()) {
                        throw new IllegalArgumentException("No messages found for label " + label);
                    }
                    for (String[] ref : refs) {
                        TextChannel channel = fetcher.getTextChannel(context.getEvent().getJDA(), ref[0]);
                        deleter.deleteMessage(channel, ref[1]);
                    }
                    Commands.getPlatform().removeGlobalMessageLabel(fullLabel);
                } else {
                    String[] ref = Commands.getPlatform().getMessageReference(fullLabel);
                    if (ref == null || ref.length != 2) {
                        throw new IllegalArgumentException("No message found for label " + label);
                    }
                    TextChannel channel = fetcher.getTextChannel(context.getEvent().getJDA(), ref[0]);
                    deleter.deleteMessage(channel, ref[1]);
                    Commands.getPlatform().removeMessageReference(fullLabel, ref[0], ref[1]);
                }
            } else {
                MessageReferenceResolver.MessageReference ref = resolver.resolve(label, channelId, messageId, context);
                TextChannel channel = fetcher.getTextChannel(context.getEvent().getJDA(), ref.channelId());
                deleter.deleteMessage(channel, ref.messageId());
            }

            if (responseMessage != null && !responseMessage.isEmpty()) {
                context.setMessageText(responseMessage);
            } else {
                context.setMessageText("Message deleted successfully.");
            }
        });
    }
}
