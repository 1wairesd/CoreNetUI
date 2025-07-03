package com.wairesd.discordbm.client.common.command;

import com.google.gson.Gson;
import com.wairesd.discordbm.api.command.Command;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.command.CommandListener;
import com.wairesd.discordbm.api.command.CommandOption;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.common.models.register.RegisterMessage;
import com.wairesd.discordbm.client.common.platform.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandRegistrationImpl implements CommandRegistration {
    
    private final Platform platform;
    private final Logger logger;
    private final Gson gson = new Gson();
    private final List<Command> registeredCommands = new ArrayList<>();
    private final Map<String, CommandHandlerWrapper> commandHandlers = new ConcurrentHashMap<>();

    public CommandRegistrationImpl(Platform platform, Logger logger) {
        this.platform = platform;
        this.logger = logger;
    }
    
    @Override
    public void registerCommand(Command command, CommandHandler handler) {
        registerCommand(command, handler, null);
    }
    
    @Override
    public void registerCommand(Command command, CommandHandler handler, CommandListener listener) {
        CommandHandlerWrapper wrapper = new CommandHandlerWrapper(handler, listener);
        commandHandlers.put(command.getName(), wrapper);

        platform.registerCommandHandler(command.getName(), 
            (cmd, options, requestId) -> {
                handler.handleCommand(cmd, options, requestId);
                if (listener != null) {
                    listener.onCommandExecuted(cmd, options.values().toArray(new String[0]), requestId);
                }
            }, 
            null,
            new CommandAdapter(command).getInternalCommand());

        synchronized (registeredCommands) {
            registeredCommands.add(command);
        }

        if (platform.getNettyService().getNettyClient() != null && 
            platform.getNettyService().getNettyClient().isActive()) {
            sendRegistrationMessage(command);
        }
    }
    
    @Override
    public void unregisterCommand(String commandName, String pluginName) {
        commandHandlers.remove(commandName);

        synchronized (registeredCommands) {
            registeredCommands.removeIf(cmd -> cmd.getName().equals(commandName));
        }

        if (platform.getNettyService().getNettyClient() != null && 
            platform.getNettyService().getNettyClient().isActive()) {
            sendUnregisterMessage(commandName, pluginName);
        }
    }
    
    @Override
    public List<Command> getRegisteredCommands() {
        synchronized (registeredCommands) {
            return new ArrayList<>(registeredCommands);
        }
    }
    
    public CommandHandler getCommandHandler(String commandName) {
        CommandHandlerWrapper wrapper = commandHandlers.get(commandName);
        return wrapper != null ? wrapper.getHandler() : null;
    }
    
    @Override
    public Command.Builder createCommandBuilder() {
        return new CommandImpl.Builder();
    }
    
    @Override
    public CommandOption.Builder createOptionBuilder() {
        return new CommandOptionImpl.Builder();
    }

    private void sendRegistrationMessage(Command command) {
        String secret = platform.getSecretCode();
        if (secret == null || secret.isEmpty()) {
            logger.warn("Cannot register command: secret is empty!");
            return;
        }

        com.wairesd.discordbm.client.common.models.command.Command internalCommand =
            new CommandAdapter(command).getInternalCommand();
        
        RegisterMessage<com.wairesd.discordbm.client.common.models.command.Command> msg =
            new RegisterMessage.Builder<com.wairesd.discordbm.client.common.models.command.Command>()
                .type("register")
                .serverName(platform.getServerName())
                .pluginName(command.getPluginName())
                .commands(List.of(internalCommand))
                .secret(secret)
                .build();
        
        platform.getNettyService().sendNettyMessage(gson.toJson(msg));
        
        if (platform.isDebugCommandRegistrations()) {
            logger.info("Sent registration message for command: " + command.getName());
        }
    }

    private void sendUnregisterMessage(String commandName, String pluginName) {
        String secret = platform.getSecretCode();
        if (secret == null || secret.isEmpty()) {
            logger.warn("Cannot unregister command: secret is empty!");
            return;
        }
        String serverName = platform.getServerName();
        com.wairesd.discordbm.common.models.unregister.UnregisterMessage msg =
                new com.wairesd.discordbm.common.models.unregister.UnregisterMessage(serverName, pluginName, commandName, secret);
        platform.getNettyService().sendNettyMessage(gson.toJson(msg));
        if (platform.isDebugCommandRegistrations()) {
            logger.info("Sent unregister message for command: " + commandName);
        }
    }

    private static class CommandHandlerWrapper {
        private final CommandHandler handler;
        private final CommandListener listener;
        
        public CommandHandlerWrapper(CommandHandler handler, CommandListener listener) {
            this.handler = handler;
            this.listener = listener;
        }
        
        public CommandHandler getHandler() {
            return handler;
        }
    }
} 