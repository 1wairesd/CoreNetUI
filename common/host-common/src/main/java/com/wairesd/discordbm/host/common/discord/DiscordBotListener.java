package com.wairesd.discordbm.host.common.discord;

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
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

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
            requestSender.sendRequestToServer(event, servers.get(0));
        } else {
            serverSelector.sendServerSelectionMenu(event, servers);
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
