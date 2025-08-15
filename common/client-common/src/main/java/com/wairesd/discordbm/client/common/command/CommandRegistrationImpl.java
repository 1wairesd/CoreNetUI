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

public class CommandRegistrationImpl extends CommandRegistration {
    private static final String REGISTER_TYPE = "register";
    private static final String EMPTY_SECRET_WARNING = "Cannot register command: secret is empty!";
    private static final String EMPTY_SECRET_UNREGISTER_WARNING = "Cannot unregister command: secret is empty!";

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
        CommandHandlerWrapper wrapper = createCommandHandlerWrapper(handler, listener);
        storeCommandHandler(command.getName(), wrapper);
        registerCommandWithPlatform(command, handler, listener);
        addToRegisteredCommands(command);
        sendRegistrationIfConnected(command);
    }

    @Override
    public void unregisterCommand(String commandName, String pluginName) {
        removeCommandHandler(commandName);
        removeFromRegisteredCommands(commandName);
        sendUnregisterIfConnected(commandName, pluginName);
    }

    @Override
    public List<Command> getRegisteredCommands() {
        return getRegisteredCommandsCopy();
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

    private CommandHandlerWrapper createCommandHandlerWrapper(CommandHandler handler, CommandListener listener) {
        return new CommandHandlerWrapper(handler, listener);
    }

    private void storeCommandHandler(String commandName, CommandHandlerWrapper wrapper) {
        commandHandlers.put(commandName, wrapper);
    }

    private void registerCommandWithPlatform(Command command, CommandHandler handler, CommandListener listener) {
        CommandHandler compositeHandler = createCompositeHandler(handler, listener);
        com.wairesd.discordbm.client.common.models.command.Command internalCommand = createInternalCommand(command);

        platform.registerCommandHandler(command.getName(), compositeHandler, null, internalCommand);
    }

    private CommandHandler createCompositeHandler(CommandHandler handler, CommandListener listener) {
        return (cmd, options, requestId) -> {
            handler.handleCommand(cmd, options, requestId);
            if (listener != null) {
                String[] optionValues = options.values().toArray(new String[0]);
                listener.onCommandExecuted(cmd, optionValues, requestId);
            }
        };
    }

    private com.wairesd.discordbm.client.common.models.command.Command createInternalCommand(Command command) {
        return new CommandAdapter(command).getInternalCommand();
    }

    private void addToRegisteredCommands(Command command) {
        synchronized (registeredCommands) {
            registeredCommands.add(command);
        }
    }

    private void removeCommandHandler(String commandName) {
        commandHandlers.remove(commandName);
    }

    private void removeFromRegisteredCommands(String commandName) {
        synchronized (registeredCommands) {
            registeredCommands.removeIf(cmd -> cmd.getName().equals(commandName));
        }
    }

    private List<Command> getRegisteredCommandsCopy() {
        synchronized (registeredCommands) {
            return new ArrayList<>(registeredCommands);
        }
    }

    private void sendRegistrationIfConnected(Command command) {
        if (isNettyClientActive()) {
            sendRegistrationMessage(command);
        }
    }

    private void sendUnregisterIfConnected(String commandName, String pluginName) {
        if (isNettyClientActive()) {
            sendUnregisterMessage(commandName, pluginName);
        }
    }

    private boolean isNettyClientActive() {
        return platform.getNettyService().getNettyClient() != null &&
                platform.getNettyService().getNettyClient().isActive();
    }

    private void sendRegistrationMessage(Command command) {
        String secret = platform.getSecretCode();

        if (!isSecretValid(secret)) {
            logEmptySecretWarning();
            return;
        }

        RegisterMessage<com.wairesd.discordbm.client.common.models.command.Command> msg =
                buildRegistrationMessage(command, secret);

        sendMessageToNetty(msg);
        logRegistrationMessage(command.getName());
    }

    private void sendUnregisterMessage(String commandName, String pluginName) {
        String secret = platform.getSecretCode();

        if (!isSecretValid(secret)) {
            logEmptySecretUnregisterWarning();
            return;
        }

        com.wairesd.discordbm.common.models.unregister.UnregisterMessage msg =
                buildUnregisterMessage(commandName, pluginName, secret);

        sendUnregisterMessageToNetty(msg);
        logUnregisterMessage(commandName);
    }

    private boolean isSecretValid(String secret) {
        return secret != null && !secret.isEmpty();
    }

    private RegisterMessage<com.wairesd.discordbm.client.common.models.command.Command> buildRegistrationMessage(
            Command command, String secret) {
        com.wairesd.discordbm.client.common.models.command.Command internalCommand = createInternalCommand(command);

        return new RegisterMessage.Builder<com.wairesd.discordbm.client.common.models.command.Command>()
                .type(REGISTER_TYPE)
                .serverName(platform.getServerName())
                .pluginName(command.getPluginName())
                .commands(List.of(internalCommand))
                .secret(secret)
                .build();
    }

    private com.wairesd.discordbm.common.models.unregister.UnregisterMessage buildUnregisterMessage(
            String commandName, String pluginName, String secret) {
        String serverName = platform.getServerName();
        return new com.wairesd.discordbm.common.models.unregister.UnregisterMessage(
                serverName, pluginName, commandName, secret);
    }

    private void sendMessageToNetty(RegisterMessage<com.wairesd.discordbm.client.common.models.command.Command> msg) {
        String jsonMessage = gson.toJson(msg);
        platform.getNettyService().sendNettyMessage(jsonMessage);
    }

    private void sendUnregisterMessageToNetty(com.wairesd.discordbm.common.models.unregister.UnregisterMessage msg) {
        String jsonMessage = gson.toJson(msg);
        platform.getNettyService().sendNettyMessage(jsonMessage);
    }

    // Logging methods
    private void logEmptySecretWarning() {
        logger.warn(EMPTY_SECRET_WARNING);
    }

    private void logEmptySecretUnregisterWarning() {
        logger.warn(EMPTY_SECRET_UNREGISTER_WARNING);
    }

    private void logRegistrationMessage(String commandName) {
        if (platform.isDebugCommandRegistrations()) {
            logger.info("Sent registration message for command: " + commandName);
        }
    }

    private void logUnregisterMessage(String commandName) {
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

        public CommandListener getListener() {
            return listener;
        }
    }
}