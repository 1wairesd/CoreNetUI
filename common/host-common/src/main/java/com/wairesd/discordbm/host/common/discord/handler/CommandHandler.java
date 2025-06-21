package com.wairesd.discordbm.host.common.discord.handler;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.api.DiscordHost;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandExecutorFacade;
import com.wairesd.discordbm.host.common.discord.response.ResponseHelper;
import com.wairesd.discordbm.host.common.discord.request.RequestSender;
import com.wairesd.discordbm.host.common.models.command.CommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.LoggerFactory;

public class CommandHandler {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final DiscordHost discordHost;
    private final RequestSender requestSender;
    private final ResponseHelper responseHelper;
    private final CommandExecutorFacade commandExecutorFacade;

    public CommandHandler(DiscordHost discordHost, RequestSender requestSender, ResponseHelper responseHelper) {
        this.discordHost = discordHost;
        this.requestSender = requestSender;
        this.responseHelper = responseHelper;

        if (discordHost.getDiscordBotManager().getJda() != null) {
            this.commandExecutorFacade = new CommandExecutorFacade();
            logger.info("CommandExecutor initialized");
        } else {
            logger.error("Failed to initialize CommandExecutor - JDA is null!");
            this.commandExecutorFacade = null;
        }
    }

    public boolean isCommandRestrictedToDM(SlashCommandInteractionEvent event, CommandDefinition cmdDef) {
        return cmdDef != null && "dm".equals(cmdDef.context()) && event.getGuild() != null;
    }

    public void handleCustomCommand(SlashCommandInteractionEvent event, String command) {
        var customCommand = discordHost.getCommandManager().getCommand(command);
        if (customCommand != null) {
            if (commandExecutorFacade != null) {
                logger.info("Executing custom command: {}", command);
                commandExecutorFacade.execute(event, customCommand);
            } else {
                logger.error("CommandExecutor is null, cannot execute command '{}'", command);
                event.reply("Command execution failed due to internal error.")
                        .setEphemeral(true)
                        .queue();
            }
        } else {
            logger.warn("Custom command '{}' not found", command);
            event.reply("Command unavailable.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
