package com.wairesd.discordbm.host.common.models.command;

import com.wairesd.discordbm.host.common.config.configurators.Settings;
import com.wairesd.discordbm.host.common.network.NettyServer;
import io.netty.channel.Channel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wairesd.discordbm.host.common.commandbuilder.commands.processor.CommandLoader;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommandRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger("DiscordBM");
    private static final String CONTEXT_BOTH = "both";
    private static final String CONTEXT_DM = "dm";
    private static final String CONTEXT_SERVER = "server";

    private final NettyServer nettyServer;
    private final ConcurrentHashMap<String, CommandDefinition> commandDefinitions = new ConcurrentHashMap<>();
    private JDA jda;

    public CommandRegistrationService(JDA jda, NettyServer nettyServer) {
        this.jda = jda;
        this.nettyServer = nettyServer;
    }

    public void registerCommands(String serverName, List<CommandDefinition> commands, Channel channel) {
        if (!validateJdaInitialization()) {
            return;
        }

        processCommandStructures(commands);
        processCommandRegistrations(serverName, commands, channel);
    }

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    private boolean validateJdaInitialization() {
        if (jda == null) {
            logger.warn("Cannot register commands - JDA is not initialized!");
            return false;
        }
        return true;
    }

    private void processCommandStructures(List<CommandDefinition> commands) {
        CommandLoader loader = new CommandLoader();
        List<CommandStructured> structured = loader.loadFromDefinitions(commands);

        if (isPlatformCommandManagerAvailable()) {
            processStructuredCommands(structured);
        }
    }

    private boolean isPlatformCommandManagerAvailable() {
        return com.wairesd.discordbm.host.common.config.configurators.Commands.getPlatform() != null
                && com.wairesd.discordbm.host.common.config.configurators.Commands.getPlatform().getCommandManager() != null;
    }

    private void processStructuredCommands(List<CommandStructured> structured) {
        for (CommandStructured cmd : structured) {
            // Empty processing block as in original code
        }
    }

    private void processCommandRegistrations(String serverName, List<CommandDefinition> commands, Channel channel) {
        for (CommandDefinition cmd : commands) {
            if (shouldSkipCommandRegistration(cmd, serverName)) {
                continue;
            }

            registerNewCommand(cmd);
            updateServerMapping(serverName, cmd, channel);
        }
    }

    private boolean shouldSkipCommandRegistration(CommandDefinition cmd, String serverName) {
        if (commandDefinitions.containsKey(cmd.name())) {
            return handleExistingCommand(cmd, serverName);
        }
        return false;
    }

    private boolean handleExistingCommand(CommandDefinition cmd, String serverName) {
        CommandDefinition existing = commandDefinitions.get(cmd.name());
        if (!existing.equals(cmd)) {
            logCommandDefinitionConflict(cmd, serverName);
            return true;
        }
        return false;
    }

    private void logCommandDefinitionConflict(CommandDefinition cmd, String serverName) {
        if (Settings.isDebugErrors()) {
            logger.error("Command {} from server {} has different definition", cmd.name(), serverName);
        }
    }

    private void registerNewCommand(CommandDefinition cmd) {
        storeCommandDefinition(cmd);
        registerCommandWithJda(cmd);
        logCommandRegistration(cmd);
    }

    private void storeCommandDefinition(CommandDefinition cmd) {
        commandDefinitions.put(cmd.name(), cmd);
        nettyServer.getCommandDefinitions().put(cmd.name(), cmd);
    }

    private void registerCommandWithJda(CommandDefinition cmd) {
        SlashCommandData cmdData = createSlashCommandData(cmd);
        addOptionsToCommand(cmdData, cmd);
        configureCommandContext(cmdData, cmd);
        jda.upsertCommand(cmdData).queue();
    }

    private SlashCommandData createSlashCommandData(CommandDefinition cmd) {
        return Commands.slash(cmd.name(), cmd.description());
    }

    private void addOptionsToCommand(SlashCommandData cmdData, CommandDefinition cmd) {
        for (var opt : cmd.options()) {
            cmdData.addOption(
                    OptionType.valueOf(opt.type()),
                    opt.name(),
                    opt.description(),
                    opt.required()
            );
        }
    }

    private void configureCommandContext(SlashCommandData cmdData, CommandDefinition cmd) {
        switch (cmd.context()) {
            case CONTEXT_BOTH, CONTEXT_DM -> cmdData.setGuildOnly(false);
            case CONTEXT_SERVER -> cmdData.setGuildOnly(true);
            default -> {
                handleUnknownContext(cmd);
                cmdData.setGuildOnly(false);
            }
        }
    }

    private void handleUnknownContext(CommandDefinition cmd) {
        if (Settings.isDebugErrors()) {
            logger.warn("Unknown context '{}' for command '{}'. Defaulting to 'both'.",
                    cmd.context(), cmd.name());
        }
    }

    private void logCommandRegistration(CommandDefinition cmd) {
        if (Settings.isDebugCommandRegistrations()) {
            logger.info("Registered command: {} with context: {}", cmd.name(), cmd.context());
        }
    }

    private void updateServerMapping(String serverName, CommandDefinition cmd, Channel channel) {
        List<NettyServer.ServerInfo> servers = getOrCreateServerList(cmd);
        removeExistingServerEntry(servers, serverName);
        addNewServerEntry(servers, serverName, channel);
        logServerMapping(cmd, serverName);
    }

    private List<NettyServer.ServerInfo> getOrCreateServerList(CommandDefinition cmd) {
        return nettyServer.getCommandToServers().computeIfAbsent(cmd.name(), k -> new ArrayList<>());
    }

    private void removeExistingServerEntry(List<NettyServer.ServerInfo> servers, String serverName) {
        servers.removeIf(serverInfo -> serverInfo.serverName().equals(serverName));
    }

    private void addNewServerEntry(List<NettyServer.ServerInfo> servers, String serverName, Channel channel) {
        servers.add(new NettyServer.ServerInfo(serverName, channel));
    }

    private void logServerMapping(CommandDefinition cmd, String serverName) {
        int totalServers = nettyServer.getCommandToServers().get(cmd.name()).size();
        logger.info("Registered command '{}' for server '{}'. Total servers for command: {}",
                cmd.name(), serverName, totalServers);
    }
}