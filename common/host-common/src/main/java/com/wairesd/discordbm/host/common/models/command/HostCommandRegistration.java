package com.wairesd.discordbm.host.common.models.command;

import com.wairesd.discordbm.api.command.Command;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.command.CommandListener;
import com.wairesd.discordbm.api.command.CommandOption;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.host.common.discord.DiscordBotManager;
import com.wairesd.discordbm.host.common.models.option.OptionDefinition;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HostCommandRegistration extends CommandRegistration {

    private static final String DEFAULT_SERVER_NAME = "host";
    private static final String PLATFORM_MANAGER_FIELD = "platformManager";

    private final DiscordBotManager discordBotManager;
    private final List<Command> registeredCommands = new ArrayList<>();
    private final ConcurrentHashMap<String, CommandHandler> commandHandlers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CommandListener> commandListeners = new ConcurrentHashMap<>();

    public HostCommandRegistration(DiscordBotManager discordBotManager) {
        this.discordBotManager = discordBotManager;
    }

    @Override
    public void registerCommand(Command command, CommandHandler handler) {
        registerCommand(command, handler, null);
    }

    @Override
    public void registerCommand(Command command, CommandHandler handler, CommandListener listener) {
        storeCommandHandlers(command, handler, listener);
        addCommandToRegistry(command);
        registerCommandWithJda(command);
        registerCommandWithNettyServer(command);
    }

    @Override
    public void unregisterCommand(String commandName, String pluginName) {
        removeCommandHandlers(commandName);
        removeCommandFromRegistry(commandName);
    }

    @Override
    public List<Command> getRegisteredCommands() {
        synchronized (registeredCommands) {
            return new ArrayList<>(registeredCommands);
        }
    }

    @Override
    public Command.Builder createCommandBuilder() {
        return new HostCommandImpl.Builder();
    }

    @Override
    public CommandOption.Builder createOptionBuilder() {
        return new HostCommandImpl.HostCommandOptionImpl.HostCommandOptionBuilder();
    }

    public CommandHandler getHandler(String commandName) {
        return commandHandlers.get(commandName);
    }

    private NettyServer getNettyServer() {
        if (!isDiscordBotManagerValid()) {
            return null;
        }

        try {
            return extractNettyServerFromPlatformManager();
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean isDiscordBotManagerValid() {
        return discordBotManager != null && discordBotManager.getJda() != null;
    }

    private NettyServer extractNettyServerFromPlatformManager() throws Exception {
        var platformField = discordBotManager.getClass().getDeclaredField(PLATFORM_MANAGER_FIELD);
        platformField.setAccessible(true);
        var platformManager = platformField.get(discordBotManager);

        if (platformManager instanceof com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager pm) {
            return pm.getNettyServer();
        }

        return null;
    }

    private void storeCommandHandlers(Command command, CommandHandler handler, CommandListener listener) {
        commandHandlers.put(command.getName(), handler);
        if (listener != null) {
            commandListeners.put(command.getName(), listener);
        }
    }

    private void addCommandToRegistry(Command command) {
        synchronized (registeredCommands) {
            registeredCommands.add(command);
        }
    }

    private void registerCommandWithJda(Command command) {
        JDA jda = discordBotManager.getJda();
        if (jda == null) {
            return;
        }

        SlashCommandData cmdData = createSlashCommandData(command);
        addOptionsToSlashCommand(command, cmdData);
        jda.upsertCommand(cmdData).queue();
    }

    private SlashCommandData createSlashCommandData(Command command) {
        return Commands.slash(command.getName(), command.getDescription());
    }

    private void addOptionsToSlashCommand(Command command, SlashCommandData cmdData) {
        for (CommandOption opt : command.getOptions()) {
            cmdData.addOption(
                    OptionType.valueOf(opt.getType()),
                    opt.getName(),
                    opt.getDescription(),
                    opt.isRequired()
            );
        }
    }

    private void registerCommandWithNettyServer(Command command) {
        NettyServer nettyServer = getNettyServer();
        if (nettyServer == null) {
            return;
        }

        CommandDefinition commandDefinition = createCommandDefinition(command);
        registerCommandDefinitionWithNettyServer(nettyServer, commandDefinition);
        addServerInfoToCommandMapping(nettyServer, commandDefinition);
    }

    private CommandDefinition createCommandDefinition(Command command) {
        List<OptionDefinition> optionDefinitions = command.getOptions().stream()
                .map(this::convertToOptionDefinition)
                .toList();

        return new CommandDefinition(
                command.getName(),
                command.getDescription(),
                command.getContext(),
                optionDefinitions,
                null,
                List.of(),
                command.getPluginName()
        );
    }

    private OptionDefinition convertToOptionDefinition(CommandOption opt) {
        return new OptionDefinition(
                opt.getName(),
                opt.getType(),
                opt.getDescription(),
                opt.isRequired()
        );
    }

    private void registerCommandDefinitionWithNettyServer(NettyServer nettyServer, CommandDefinition commandDefinition) {
        nettyServer.getCommandDefinitions().put(commandDefinition.name(), commandDefinition);
    }

    private void addServerInfoToCommandMapping(NettyServer nettyServer, CommandDefinition commandDefinition) {
        String serverName = getServerName(nettyServer);
        NettyServer.ServerInfo serverInfo = new NettyServer.ServerInfo(serverName, null);

        nettyServer.getCommandToServers()
                .computeIfAbsent(commandDefinition.name(), k -> new ArrayList<>())
                .add(serverInfo);
    }

    private String getServerName(NettyServer nettyServer) {
        JDA jda = nettyServer.getJda();
        return (jda != null) ? jda.getSelfUser().getName() : DEFAULT_SERVER_NAME;
    }

    private void removeCommandHandlers(String commandName) {
        commandHandlers.remove(commandName);
        commandListeners.remove(commandName);
    }

    private void removeCommandFromRegistry(String commandName) {
        synchronized (registeredCommands) {
            registeredCommands.removeIf(cmd -> cmd.getName().equals(commandName));
        }
    }
}