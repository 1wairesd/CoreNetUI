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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HostCommandRegistration implements CommandRegistration {
    private final DiscordBotManager discordBotManager;
    private final List<Command> registeredCommands = new ArrayList<>();
    private final ConcurrentHashMap<String, CommandHandler> commandHandlers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CommandListener> commandListeners = new ConcurrentHashMap<>();

    public HostCommandRegistration(DiscordBotManager discordBotManager) {
        this.discordBotManager = discordBotManager;
    }

    private NettyServer getNettyServer() {
        if (discordBotManager != null && discordBotManager.getJda() != null) {
            try {
                var platformField = discordBotManager.getClass().getDeclaredField("platformManager");
                platformField.setAccessible(true);
                var platformManager = platformField.get(discordBotManager);
                if (platformManager instanceof com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager pm) {
                    return pm.getNettyServer();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    @Override
    public void registerCommand(Command command, CommandHandler handler) {
        registerCommand(command, handler, null);
    }

    @Override
    public void registerCommand(Command command, CommandHandler handler, CommandListener listener) {
        commandHandlers.put(command.getName(), handler);
        if (listener != null) {
            commandListeners.put(command.getName(), listener);
        }
        synchronized (registeredCommands) {
            registeredCommands.add(command);
        }
        JDA jda = discordBotManager.getJda();
        if (jda != null) {
            var cmdData = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(command.getName(), command.getDescription());
            for (CommandOption opt : command.getOptions()) {
                cmdData.addOption(
                        net.dv8tion.jda.api.interactions.commands.OptionType.valueOf(opt.getType()),
                        opt.getName(),
                        opt.getDescription(),
                        opt.isRequired()
                );
            }
            jda.upsertCommand(cmdData).queue();
        }
        NettyServer nettyServer = getNettyServer();
        if (nettyServer != null) {
            CommandDefinition def = new CommandDefinition(
                command.getName(),
                command.getDescription(),
                command.getContext(),
                command.getOptions().stream().map(opt ->
                    new OptionDefinition(opt.getName(), opt.getType(), opt.getDescription(), opt.isRequired())
                ).toList(),
                null,
                List.of(),
                command.getPluginName()
            );
            nettyServer.getCommandDefinitions().put(def.name(), def);
            String serverName = nettyServer.getJda() != null ? nettyServer.getJda().getSelfUser().getName() : "host";
            NettyServer.ServerInfo serverInfo = new NettyServer.ServerInfo(serverName, null);
            nettyServer.getCommandToServers()
                .computeIfAbsent(def.name(), k -> new ArrayList<>())
                .add(serverInfo);
        }
    }

    @Override
    public void unregisterCommand(String commandName, String pluginName) {
        commandHandlers.remove(commandName);
        commandListeners.remove(commandName);
        synchronized (registeredCommands) {
            registeredCommands.removeIf(cmd -> cmd.getName().equals(commandName));
        }
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
} 