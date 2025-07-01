package com.wairesd.discordbm.host.common.discord;

import com.wairesd.discordbm.api.interaction.InteractionResponseType;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.error.CommandErrorHandler;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.commandbuilder.core.parser.CommandParserCondition;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.response.CommandResponder;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.validator.CommandValidator;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.discord.handler.CommandHandler;
import com.wairesd.discordbm.host.common.discord.request.RequestSender;
import com.wairesd.discordbm.host.common.discord.response.ResponseHelper;
import com.wairesd.discordbm.host.common.discord.selection.ServerSelector;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.wairesd.discordbm.host.common.config.configurators.CommandEphemeral;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordBotListener extends ListenerAdapter {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final DiscordBMHPlatformManager platformManager;
    private final NettyServer nettyServer;

    private final CommandHandler commandHandler;
    private final ServerSelector serverSelector;
    private final RequestSender requestSender;
    private final ResponseHelper responseHelper;
    public static final Map<String, Boolean> formEphemeralMap = new ConcurrentHashMap<>();

    public DiscordBotListener(DiscordBMHPlatformManager platformManager, NettyServer nettyServer, PluginLogger logger) {
        this.platformManager = platformManager;
        this.nettyServer = nettyServer;
        this.requestSender = new RequestSender(nettyServer, DiscordBotListener.logger);
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
                new CommandErrorHandler(null, event)
                    .handleRoleRequired(cmdDef.permission());
                return;
            }
        }

        if (cmdDef != null && cmdDef.conditions() != null && !cmdDef.conditions().isEmpty()) {
            var conditions = cmdDef.conditions().stream()
                .map(CommandParserCondition::parseCondition)
                .toList();
            var commandStructured = new CommandStructured(
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
            var validator = new CommandValidator();
            var context = new Context(event);
            if (!validator.validateConditions(commandStructured, context)) {
                new CommandResponder()
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
            boolean ephemeral;
            java.util.UUID requestId = java.util.UUID.randomUUID();
            Map<String, String> options = event.getOptions().stream()
                .collect(Collectors.toMap(o -> o.getName(), o -> o.getAsString()));
            Boolean configEphemeral = CommandEphemeral.getEphemeralForCommand(command, options);
            ephemeral = configEphemeral != null ? configEphemeral : false;
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
            if (useDeferReply) {
                final boolean finalRequiresModal = requiresModal;
                final boolean finalUseDeferReply = useDeferReply;
                final java.util.UUID finalRequestId = requestId;
                final boolean finalEphemeral = ephemeral;
                event.deferReply(ephemeral).queue(hook -> {
                    requestSender.sendRequestToServer(event, servers.get(0), finalRequiresModal, finalUseDeferReply, finalRequestId, finalEphemeral);
                });
                return;
            }
            requestSender.sendRequestToServer(event, servers.get(0), requiresModal, useDeferReply, requestId, ephemeral);
        } else {
            serverSelector.sendServerSelectionMenu(event, servers);
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        if (!modalId.contains("_form_")) {
            return;
        }

        try {
            Object handler = platformManager.getFormHandlers().get(modalId);
            if (handler == null) {
                event.reply("Form has expired or is invalid.").setEphemeral(true).queue();
                return;
            }

            Map<String, String> responses = event.getValues().stream()
                    .collect(Collectors.toMap(
                            input -> input.getId(),
                            input -> input.getAsString()
                    ));

            String requestId = null;
            String command = null;
            if (modalId.contains("_form_")) {
                int idx = modalId.lastIndexOf("_form_");
                command = modalId.substring(0, idx);
                requestId = modalId.substring(idx + 6);
            }
            final String finalRequestId = requestId;
            final String finalCommand = command;
            Boolean configEphemeralLog = CommandEphemeral.getEphemeralForCommand(finalCommand, responses);
            final boolean ephemeral;
            Boolean configEphemeral = CommandEphemeral.getEphemeralForCommand(finalCommand, responses);
            ephemeral = configEphemeral != null ? configEphemeral : false;
            if (finalRequestId != null && finalCommand != null) {
                var nettyServer = platformManager.getNettyServer();
                var servers = nettyServer.getServersForCommand(finalCommand);
                if (servers != null && !servers.isEmpty()) {
                    var channel = servers.get(0).channel();

                    event.deferReply(ephemeral).queue(hook -> {
                        requestSender.storeInteractionHook(java.util.UUID.fromString(finalRequestId), hook);

                        Map<String, Object> msg = new java.util.HashMap<>();
                        msg.put("type", "form_submit");
                        msg.put("command", finalCommand);
                        msg.put("requestId", finalRequestId);
                        msg.put("formData", responses);
                        msg.put("ephemeral", ephemeral);
                        String json = new com.google.gson.Gson().toJson(msg);
                        nettyServer.sendMessage(channel, json);
                    });
                } else {
                    event.reply("Ошибка: сервер не найден.").setEphemeral(true).queue();
                }
            } else {
                event.reply("Не удалось определить команду или requestId для формы.").setEphemeral(true).queue();
            }
        } catch (Exception e) {
            event.reply("An error occurred while processing the form.").setEphemeral(true).queue();
        } finally {
            platformManager.getFormHandlers().remove(modalId);
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
