package com.wairesd.discordbm.host.common.commandbuilder.interaction.messages;

import com.wairesd.discordbm.host.common.commandbuilder.core.channel.ChannelFetcher;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.utils.message.MessageDeleter;
import com.wairesd.discordbm.host.common.commandbuilder.utils.message.MessageReferenceResolver;
import com.wairesd.discordbm.host.common.config.configurators.Commands;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DeleteMessageAction implements CommandAction {
    private final String label;
    private final String channelId;
    private final String messageId;
    private final String responseMessage;

    private final MessageReferenceResolver resolver = new MessageReferenceResolver();
    private final ChannelFetcher fetcher = new ChannelFetcher();
    private final MessageDeleter deleter = new MessageDeleter();

    public DeleteMessageAction(Map<String, Object> properties) {
        this.label = (String) properties.get("label");
        this.channelId = (String) properties.get("channel_id");
        this.messageId = (String) properties.get("message_id");
        this.responseMessage = (String) properties.get("response_message");
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        return CompletableFuture.runAsync(() -> {
            MessageReferenceResolver.MessageReference ref = resolver.resolve(label, channelId, messageId, context);
            TextChannel channel = fetcher.getTextChannel(context.getEvent().getJDA(), ref.channelId());
            deleter.deleteMessage(channel, ref.messageId());
            
            if (label != null) {
                String fullLabel = (context.getEvent().getGuild() != null ? context.getEvent().getGuild().getId() : "DM") + "_" + label;
                Commands.getPlatform().removeGlobalMessageLabel(fullLabel);
            }
            
            if (responseMessage != null && !responseMessage.isEmpty()) {
                context.setMessageText(responseMessage);
            } else {
                context.setMessageText("Message deleted successfully.");
            }
        });
    }
}
