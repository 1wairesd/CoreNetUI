package com.wairesd.discordbm.host.common.discord.handler;

import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandExecutorFacade;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.discord.response.ResponseHelper;
import com.wairesd.discordbm.host.common.discord.request.RequestSender;
import com.wairesd.discordbm.host.common.models.command.CommandDefinition;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.host.common.models.command.HostCommandRegistration;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Collectors;

public class CommandHandler {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private final DiscordBMHPlatformManager platformManager;
    private final RequestSender requestSender;
    private final ResponseHelper responseHelper;
    private final CommandExecutorFacade commandExecutorFacade;

    public CommandHandler(DiscordBMHPlatformManager platformManager, RequestSender requestSender,
                          ResponseHelper responseHelper) {
        this.platformManager = platformManager;
        this.requestSender = requestSender;
        this.responseHelper = responseHelper;
        this.commandExecutorFacade = initializeCommandExecutor();
    }

    private CommandExecutorFacade initializeCommandExecutor() {
        if (platformManager.getDiscordBotManager().getJda() != null) {
            logger.info("CommandExecutor initialized");
            return new CommandExecutorFacade();
        } else {
            logger.error("Failed to initialize CommandExecutor - JDA is null!");
            return null;
        }
    }

    public boolean isCommandRestrictedToDM(SlashCommandInteractionEvent event, CommandDefinition cmdDef) {
        return cmdDef != null && "dm".equals(cmdDef.context()) && event.getGuild() != null;
    }

    public void handleCustomCommand(SlashCommandInteractionEvent event, String command) {
        if (executeLocalCommand(event, command)) {
            return;
        }
        if (executeRegisteredCommand(event, command)) {
            return;
        }
        replyCommandUnavailable(event, command);
    }

    private boolean executeLocalCommand(SlashCommandInteractionEvent event, String command) {
        var customCommand = platformManager.getCommandManager().getCommand(command);
        if (customCommand == null) {
            return false;
        }
        if (commandExecutorFacade == null) {
            handleExecutorNull(event, command);
            return true;
        }
        if (Settings.isDebugCommandExecution()) {
            logger.info("Executing custom command: {}", command);
        }
        commandExecutorFacade.execute(event, customCommand);
        return true;
    }

    private boolean executeRegisteredCommand(SlashCommandInteractionEvent event, String command) {
        var api = DBMAPI.getInstance();
        if (api == null) {
            return false;
        }
        CommandRegistration reg = api.getCommandRegistration();
        var registered = reg.getRegisteredCommands();
        for (var cmd : registered) {
            if (cmd.getName().equalsIgnoreCase(command) && reg instanceof HostCommandRegistration hostReg) {
                return handleHostCommand(event, command, hostReg);
            }
        }
        return false;
    }

    private boolean handleHostCommand(SlashCommandInteractionEvent event, String command,
                                      HostCommandRegistration hostReg) {
        var handler = hostReg.getHandler(command);
        if (handler == null) {
            return false;
        }
        String reqId = UUID.randomUUID().toString();
        event.deferReply(false).queue(hook -> {
            requestSender.getPendingRequests().put(UUID.fromString(reqId), event);
            handler.handleCommand(command, event.getOptions().stream()
                    .collect(Collectors.toMap(o -> o.getName(), o -> o.getAsString())), reqId);
        });
        return true;
    }

    private void handleExecutorNull(SlashCommandInteractionEvent event, String command) {
        logger.error("CommandExecutor is null, cannot execute command '{}'", command);
        event.reply("Command execution failed due to internal error.")
                .setEphemeral(true)
                .queue();
    }

    private void replyCommandUnavailable(SlashCommandInteractionEvent event, String command) {
        if (Settings.isDebugCommandNotFound()) {
            logger.warn("Custom command '{}' not found", command);
        }
        event.reply("Command unavailable.")
                .setEphemeral(true)
                .queue();
    }
}
