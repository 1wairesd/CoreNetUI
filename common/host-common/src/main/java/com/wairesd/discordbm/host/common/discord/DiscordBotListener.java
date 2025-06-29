package com.wairesd.discordbm.host.common.discord;

import com.wairesd.discordbm.api.interaction.InteractionResponseType;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.discord.handler.CommandHandler;
import com.wairesd.discordbm.host.common.discord.request.RequestSender;
import com.wairesd.discordbm.host.common.discord.response.ResponseHelper;
import com.wairesd.discordbm.host.common.discord.selection.ServerSelector;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.Map;

public class DiscordBotListener extends ListenerAdapter {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final DiscordBMHPlatformManager platformManager;
    private final NettyServer nettyServer;

    private final CommandHandler commandHandler;
    private final ServerSelector serverSelector;
    private final RequestSender requestSender;
    private final ResponseHelper responseHelper;

    public DiscordBotListener(DiscordBMHPlatformManager platformManager, NettyServer nettyServer, PluginLogger logger) {
        this.platformManager = platformManager;
        this.nettyServer = nettyServer;

        this.requestSender = new RequestSender(nettyServer, logger);
        this.responseHelper = new ResponseHelper();
        this.commandHandler = new CommandHandler(platformManager, requestSender, responseHelper);
        this.serverSelector = new ServerSelector(requestSender, responseHelper);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (Settings.isDebugCommandReceived()) {
            logger.info("Received slash command: {}", command);
        }

        if (Settings.isDebugCommandReceived()) {
            logger.info("Checking servers for command '{}': {}", command, nettyServer.getServersForCommand(command).stream().map(NettyServer.ServerInfo::serverName).collect(Collectors.toList()));
        }

        var servers = nettyServer.getServersForCommand(command);

        if (servers.isEmpty()) {
            commandHandler.handleCustomCommand(event, command);
            return;
        }

        var cmdDef = nettyServer.getCommandDefinitions().get(command);
        if (cmdDef != null && cmdDef.permission() != null && !cmdDef.permission().isEmpty()) {
            var member = event.getMember();
            var memberRoles = member != null ? member.getRoles().stream().map(r -> r.getId()).toList() : java.util.Collections.emptyList();

            boolean hasRole = member != null && member.getRoles().stream()
                .anyMatch(role -> role.getId().equals(cmdDef.permission()));
            if (!hasRole) {
                new com.wairesd.discordbm.host.common.commandbuilder.core.models.error.CommandErrorHandler(null, event)
                    .handleRoleRequired(cmdDef.permission());
                return;
            }
        }

        if (cmdDef != null && cmdDef.conditions() != null && !cmdDef.conditions().isEmpty()) {
            var conditions = cmdDef.conditions().stream()
                .map(com.wairesd.discordbm.host.common.commandbuilder.core.parser.CommandParserCondition::parseCondition)
                .toList();
            var commandStructured = new com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured(
                cmdDef.name(),
                cmdDef.description(),
                cmdDef.context(),
                java.util.List.of(),
                conditions,
                java.util.List.of(),
                java.util.List.of(),
                null,
                cmdDef.permission()
            );
            var validator = new com.wairesd.discordbm.host.common.commandbuilder.interaction.validator.CommandValidator();
            var context = new com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context(event);
            if (!validator.validateConditions(commandStructured, context)) {
                new com.wairesd.discordbm.host.common.commandbuilder.interaction.response.CommandResponder()
                    .handleFailedValidation(event, commandStructured, context);
                return;
            }
        }

        if (commandHandler.isCommandRestrictedToDM(event, cmdDef)) {
            responseHelper.replyCommandRestrictedToDM(event);
            return;
        }

        if (servers.size() == 1) {
            InteractionResponseType responseType = InteractionResponseType.AUTO;
            boolean requiresModal = false;
            boolean useDeferReply = false;
            boolean useReply = false;
            switch (responseType) {
                case REPLY_MODAL -> requiresModal = true;
                case DEFER_REPLY -> useDeferReply = true;
                case REPLY -> useReply = true;
                case AUTO -> {
                    if (event.getOptions().isEmpty()) {
                        requiresModal = true;
                    } else {
                        useDeferReply = true;
                    }
                }
            }
            if (useReply) {
                event.reply("...").queue();
                return;
            }
            requestSender.sendRequestToServer(event, servers.get(0), requiresModal, useDeferReply);
        } else {
            serverSelector.sendServerSelectionMenu(event, servers);
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        logger.info("[onModalInteraction] modalId: {}", modalId);
        if (!modalId.contains("_form_")) {
            logger.warn("[onModalInteraction] modalId does not contain '_form_': {}", modalId);
            return;
        }

        try {
            Object handler = platformManager.getFormHandlers().get(modalId);
            logger.info("[onModalInteraction] handler found: {}", handler != null);
            if (handler == null) {
                logger.warn("No handler found for modal ID: {}", modalId);
                event.reply("Form has expired or is invalid.").setEphemeral(true).queue();
                return;
            }

            Map<String, String> responses = event.getValues().stream()
                    .collect(Collectors.toMap(
                            input -> input.getId(),
                            input -> input.getAsString()
                    ));
            logger.info("[onModalInteraction] responses: {}", responses);

            String requestId = null;
            String command = null;
            if (modalId.contains("_form_")) {
                int idx = modalId.lastIndexOf("_form_");
                command = modalId.substring(0, idx);
                requestId = modalId.substring(idx + 6);
            }
            final String finalRequestId = requestId;
            final String finalCommand = command;
            logger.info("[onModalInteraction] command: {}, requestId: {}", finalCommand, finalRequestId);
            if (finalRequestId != null && finalCommand != null) {
                var nettyServer = platformManager.getNettyServer();
                var servers = nettyServer.getServersForCommand(finalCommand);
                logger.info("[onModalInteraction] servers for command '{}': {}", finalCommand, servers);
                if (servers != null && !servers.isEmpty()) {
                    var channel = servers.get(0).channel();

                    event.deferReply(true).queue(hook -> {
                        requestSender.storeInteractionHook(java.util.UUID.fromString(finalRequestId), hook);
                        logger.info("[onModalInteraction] Stored interaction hook for requestId: {}", finalRequestId);

                        Map<String, Object> msg = new java.util.HashMap<>();
                        msg.put("type", "form_submit");
                        msg.put("command", finalCommand);
                        msg.put("requestId", finalRequestId);
                        msg.put("formData", responses);
                        String json = new com.google.gson.Gson().toJson(msg);
                        logger.info("[onModalInteraction] sending to client: {}", json);
                        nettyServer.sendMessage(channel, json);
                    });
                } else {
                    logger.warn("[onModalInteraction] No servers found for command: {}", finalCommand);
                    event.reply("Ошибка: сервер не найден.").setEphemeral(true).queue();
                }
                logger.info("[onModalInteraction] deferReply sent to Discord");
            } else {
                logger.warn("[onModalInteraction] Could not determine command or requestId for modalId: {}", modalId);
                event.reply("Не удалось определить команду или requestId для формы.").setEphemeral(true).queue();
            }
        } catch (Exception e) {
            logger.error("[onModalInteraction] Modal Window Processing Error", e);
            event.reply("An error occurred while processing the form.").setEphemeral(true).queue();
        } finally {
            platformManager.getFormHandlers().remove(modalId);
            logger.info("[onModalInteraction] handler removed for modalId: {}", modalId);
        }
    }

    public RequestSender getRequestSender() {
        return requestSender;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!serverSelector.isValidSelectMenu(event)) return;

        serverSelector.handleSelection(event);
    }
}
