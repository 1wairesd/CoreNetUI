package com.wairesd.discordbm.host.common.discord;

import com.wairesd.discordbm.api.interaction.InteractionResponseType;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.conditions.CommandCondition;
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
import com.wairesd.discordbm.host.common.models.command.CommandDefinition;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DiscordBotListener extends ListenerAdapter {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private final DiscordBMHPlatformManager platformManager;
    private final NettyServer nettyServer;
    private final CommandHandler commandHandler;
    private final ServerSelector serverSelector;
    private final RequestSender requestSender;
    private final ResponseHelper responseHelper;
    public static final Map<String, Boolean> formEphemeralMap = new ConcurrentHashMap<>();
    private final Map<String, String> requestIdToCommand;

    public DiscordBotListener(DiscordBMHPlatformManager platformManager, NettyServer nettyServer,
                              PluginLogger logger, Map<String, String> requestIdToCommand) {
        this.platformManager = platformManager;
        this.nettyServer = nettyServer;
        this.requestSender = new RequestSender(nettyServer, DiscordBotListener.logger);
        this.responseHelper = new ResponseHelper();
        this.commandHandler = new CommandHandler(platformManager, requestSender, responseHelper);
        this.serverSelector = new ServerSelector(requestSender, responseHelper);
        this.requestIdToCommand = requestIdToCommand;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        logCommandReceived(command);

        List<NettyServer.ServerInfo> servers = getServersForCommand(command);

        if (servers.isEmpty()) {
            commandHandler.handleCustomCommand(event, command);
            return;
        }

        var cmdDef = nettyServer.getCommandDefinitions().get(command);

        if (!hasRequiredPermission(event, cmdDef)) {
            return;
        }

        if (!validateCommandConditions(event, cmdDef)) {
            return;
        }

        if (commandHandler.isCommandRestrictedToDM(event, cmdDef)) {
            responseHelper.replyCommandRestrictedToDM(event);
            return;
        }

        if (servers.size() == 1) {
            handleSingleServerCommand(event, command, servers.get(0));
        } else {
            serverSelector.sendServerSelectionMenu(event, servers);
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!serverSelector.isValidSelectMenu(event)) return;
        serverSelector.handleSelection(event);
    }

    public Map<String, String> getRequestIdToCommand() {
        return requestIdToCommand;
    }

    public RequestSender getRequestSender() {
        return requestSender;
    }

    private void logCommandReceived(String command) {
        if (Settings.isDebugCommandReceived()) {
            logger.info("Received slash command: {}", command);
        }
    }

    private List<NettyServer.ServerInfo> getServersForCommand(String command) {
        var servers = nettyServer.getServersForCommand(command);

        if (Settings.isDebugCommandReceived()) {
            logger.info("Checking servers for command '{}': {}", command,
                    servers.stream().map(NettyServer.ServerInfo::serverName).collect(Collectors.toList()));
        }

        return servers;
    }

    private boolean hasRequiredPermission(SlashCommandInteractionEvent event,
                                          CommandDefinition cmdDef) {
        if (cmdDef == null || cmdDef.permission() == null || cmdDef.permission().isEmpty()) {
            return true;
        }

        var member = event.getMember();
        boolean hasRole = member != null && member.getRoles().stream()
                .anyMatch(role -> role.getId().equals(cmdDef.permission()));

        if (!hasRole) {
            new CommandErrorHandler(null, event).handleRoleRequired(cmdDef.permission());
        }

        return hasRole;
    }

    private boolean validateCommandConditions(SlashCommandInteractionEvent event,
                                              CommandDefinition cmdDef) {
        if (cmdDef == null || cmdDef.conditions() == null || cmdDef.conditions().isEmpty()) {
            return true;
        }

        var conditions = cmdDef.conditions().stream()
                .map(CommandParserCondition::parseCondition)
                .map(c -> (CommandCondition) c)
                .toList();

        var commandStructured = createCommandStructured(cmdDef, conditions);
        var validator = new CommandValidator();
        var context = new Context(event);

        boolean isValid = validator.validateConditions(commandStructured, context);
        if (!isValid) {
            new CommandResponder().handleFailedValidation(event, commandStructured, context);
        }

        return isValid;
    }

    private CommandStructured createCommandStructured(CommandDefinition cmdDef,
                                                      List<CommandCondition> conditions) {
        return new CommandStructured(
                cmdDef.name(),
                cmdDef.description(),
                cmdDef.context(),
                List.of(),
                conditions,
                List.of(),
                List.of(),
                cmdDef.permission(),
                cmdDef.pluginName()
        );
    }

    private void handleSingleServerCommand(SlashCommandInteractionEvent event, String command,
                                           NettyServer.ServerInfo server) {
        InteractionResponseType responseType = determineResponseTypeForCommand(command, event);
        CommandExecutionParams params = createExecutionParams(responseType);
        UUID requestId = UUID.randomUUID();

        if (params.useDeferReply()) {
            handleDeferredReply(event, server, params, requestId);
        } else {
            executeCommand(event, server, params, requestId);
            if (params.requiresModal()) {
                requestIdToCommand.put(requestId.toString(), command);
            }
        }
    }

    private CommandExecutionParams createExecutionParams(InteractionResponseType responseType) {
        boolean requiresModal = false;
        boolean useDeferReply = false;
        boolean ephemeral = false;

        switch (responseType) {
            case REPLY_MODAL -> requiresModal = true;
            case DEFER_REPLY -> useDeferReply = true;
            case AUTO -> {
                requiresModal = true;
                useDeferReply = true;
            }
        }

        return new CommandExecutionParams(requiresModal, useDeferReply, ephemeral);
    }

    private void handleDeferredReply(SlashCommandInteractionEvent event, NettyServer.ServerInfo server,
                                     CommandExecutionParams params, UUID requestId) {
        event.deferReply(params.ephemeral()).queue(hook -> {
            executeCommand(event, server, params, requestId);
        });
    }

    private void executeCommand(SlashCommandInteractionEvent event, NettyServer.ServerInfo server,
                                CommandExecutionParams params, UUID requestId) {
        requestSender.sendRequestToServer(event, server, params.requiresModal(),
                params.useDeferReply(), requestId, params.ephemeral());
    }

    private InteractionResponseType determineResponseTypeForCommand(String command,
                                                                    SlashCommandInteractionEvent event) {
        boolean hasOptions = !event.getOptions().isEmpty();
        var cmdDef = nettyServer.getCommandDefinitions().get(command);

        if (cmdDef == null) {
            return InteractionResponseType.AUTO;
        }

        if (isFormCommand(cmdDef)) {
            return InteractionResponseType.REPLY_MODAL;
        }

        if (shouldUseDeferReply(cmdDef, hasOptions)) {
            return InteractionResponseType.DEFER_REPLY;
        }

        return InteractionResponseType.AUTO;
    }

    private boolean isFormCommand(CommandDefinition cmdDef) {
        return cmdDef.context() != null && cmdDef.context().contains("form");
    }

    private boolean shouldUseDeferReply(CommandDefinition cmdDef, boolean hasOptions) {
        if (hasOptions && cmdDef.options() != null && cmdDef.options().size() > 2) {
            return true;
        }

        return (cmdDef.options() == null || cmdDef.options().isEmpty()) &&
                (cmdDef.context() == null || !cmdDef.context().contains("form"));
    }

    private record CommandExecutionParams(boolean requiresModal, boolean useDeferReply, boolean ephemeral) {}
}