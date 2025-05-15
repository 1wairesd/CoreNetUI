package com.wairesd.discordbm.velocity.discord.handle;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.CommandExecutor;
import com.wairesd.discordbm.velocity.discord.response.ResponseHelper;
import com.wairesd.discordbm.velocity.discord.request.RequestSender;
import com.wairesd.discordbm.velocity.models.command.CommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

public class CommandHandler {
    private final DiscordBMV plugin;
    private final Logger logger;
    private final RequestSender requestSender;
    private final ResponseHelper responseHelper;
    private final CommandExecutor commandExecutor;

    public CommandHandler(DiscordBMV plugin, Logger logger, RequestSender requestSender, ResponseHelper responseHelper) {
        this.plugin = plugin;
        this.logger = logger;
        this.requestSender = requestSender;
        this.responseHelper = responseHelper;

        if (plugin.getDiscordBotManager().getJda() != null) {
            this.commandExecutor = new CommandExecutor();
            logger.info("CommandExecutor initialized successfully");
        } else {
            logger.error("Failed to initialize CommandExecutor - JDA is null!");
            this.commandExecutor = null;
        }
    }

    public boolean isCommandRestrictedToDM(SlashCommandInteractionEvent event, CommandDefinition cmdDef) {
        return cmdDef != null && "dm".equals(cmdDef.context()) && event.getGuild() != null;
    }

    public void handleCustomCommand(SlashCommandInteractionEvent event, String command) {
        var customCommand = plugin.getCommandManager().getCommand(command);
        if (customCommand != null) {
            if (commandExecutor != null) {
                logger.info("Executing custom command: {}", command);
                commandExecutor.execute(event, customCommand);
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
