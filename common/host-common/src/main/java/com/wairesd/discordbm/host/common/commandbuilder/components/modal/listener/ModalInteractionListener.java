package com.wairesd.discordbm.host.common.commandbuilder.components.modal.listener;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModalInteractionListener extends ListenerAdapter {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private final DiscordBMHPlatformManager platformManager;
    private final Map<String, String> requestIdToCommand;

    public ModalInteractionListener(DiscordBMHPlatformManager platformManager, Map<String, String> requestIdToCommand) {
        this.platformManager = platformManager;
        this.requestIdToCommand = requestIdToCommand;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalID = event.getModalId();
        if (!modalID.startsWith("modal_")) return;

        try {
            Object handler = platformManager.getFormHandlers().get(modalID);
            if (handler != null) {
                Map<String, String> responses = event.getValues().stream()
                        .collect(Collectors.toMap(
                                input -> input.getId(),
                                input -> input.getAsString()
                        ));
                if (handler instanceof Pair) {
                    Pair<CompletableFuture<Void>, Context> pair = (Pair<CompletableFuture<Void>, Context>) handler;
                    Context context = pair.getRight();
                    context.setFormResponses(responses);
                    context.setHook(event.getHook());
                    event.deferReply(false).queue();
                    pair.getLeft().complete(null);
                } else if (handler instanceof String) {
                    String messageTemplate = (String) handler;
                    String messageWithFormPlaceholders = replacePlaceholders(messageTemplate, responses);
                    Context context = new Context(event);
                    MessageFormatterUtils.format(messageWithFormPlaceholders, event, context, false)
                            .thenAccept(formatted -> event.reply(formatted).setEphemeral(true).queue());
                }
                platformManager.getFormHandlers().remove(modalID);
                return;
            }
            String requestId = modalID.substring(5);
            String command = requestIdToCommand.get(requestId);
            if (command == null) {
                return;
            }
            requestIdToCommand.remove(requestId);
            Map<String, String> responses = event.getValues().stream()
                    .collect(Collectors.toMap(
                            input -> input.getId(),
                            input -> input.getAsString()
                    ));
            final boolean ephemeral = false;
            var nettyServer = platformManager.getNettyServer();
            var servers = nettyServer.getServersForCommand(command);
            if (servers != null && !servers.isEmpty()) {
                var channel = servers.get(0).channel();
                event.deferReply(ephemeral).queue(hook -> {
                    Map<String, Object> msg = new java.util.HashMap<>();
                    msg.put("type", "modal_submit");
                    msg.put("command", command);
                    msg.put("requestId", requestId);
                    msg.put("modalData", responses);
                    msg.put("ephemeral", ephemeral);
                    String json = new com.google.gson.Gson().toJson(msg);
                    nettyServer.sendMessage(channel, json);
                    hook.deleteOriginal().queue();
                }, error -> {
                    logger.error("Failed to deferReply in onModalInteraction", error);
                });
            }
            platformManager.getFormHandlers().remove(modalID);
            return;
        } catch (Exception e) {
            logger.error("Modal Window Processing Error", e);
            event.reply("An error occurred while processing the form.").setEphemeral(true).queue();
        }
    }

    private String replacePlaceholders(String template, Map<String, String> responses) {
        for (Map.Entry<String, String> entry : responses.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}