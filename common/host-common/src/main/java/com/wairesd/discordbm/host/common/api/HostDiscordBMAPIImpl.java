package com.wairesd.discordbm.host.common.api;

import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.component.ComponentHandler;
import com.wairesd.discordbm.api.embed.EmbedBuilder;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.message.ResponseType;
import com.wairesd.discordbm.api.role.RoleManager;
import com.wairesd.discordbm.api.form.FormBuilder;
import com.wairesd.discordbm.api.form.FormFieldBuilder;
import com.wairesd.discordbm.common.component.ButtonImpl;
import com.wairesd.discordbm.common.embed.EmbedBuilderImpl;
import com.wairesd.discordbm.common.form.FormBuilderImpl;
import com.wairesd.discordbm.common.form.FormFieldBuilderImpl;
import com.wairesd.discordbm.common.logging.LoggerAdapter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.common.event.EventBusImpl;
import com.wairesd.discordbm.host.common.config.configurators.CommandEphemeral;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HostDiscordBMAPIImpl extends DBMAPI {
    private final CommandRegistration commandRegistration;
    private final MessageSender messageSender;
    private final LoggerAdapter logger;
    private final ComponentRegistry componentRegistry;
    private final EventBus eventBus;
    private final RoleManager roleManager;
    private final long startTime = System.currentTimeMillis();
    private ResponseType currentResponseType;
    private final Map<String, Boolean> localEphemeralRules = new ConcurrentHashMap<>();

    public HostDiscordBMAPIImpl(CommandRegistration commandRegistration, MessageSender messageSender) {
        this.commandRegistration = commandRegistration;
        this.messageSender = messageSender;
        PluginLogger pluginLogger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
        this.logger = new LoggerAdapter(pluginLogger);
        this.componentRegistry = new HostComponentRegistry(logger);
        this.eventBus = new EventBusImpl();
        this.roleManager = new HostRoleManager();
    }

    @Override
    public CommandRegistration getCommandRegistration() {
        return commandRegistration;
    }

    @Override
    public MessageSender getMessageSender() {
        return messageSender;
    }

    @Override
    public ComponentRegistry getComponentRegistry() {
        return componentRegistry;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public EmbedBuilder createEmbedBuilder() {
        return new EmbedBuilderImpl();
    }

    @Override
    public FormBuilder createFormBuilder() {
        return new FormBuilderImpl();
    }

    @Override
    public FormFieldBuilder createFormFieldBuilder() {
        return new FormFieldBuilderImpl();
    }

    @Override
    public String getServerName() {
        return "Host";
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public RoleManager getRoleManager() {
        return roleManager;
    }

    @Override
    public void registerEphemeralRules(Map<String, Boolean> rules) {
        if (rules != null) {
            localEphemeralRules.putAll(rules);
        }
    }

    /**
     * Checks if an ephemeral response should be used for the command
     * @param command the command name
     * @param options command parameters
     * @return true if ephemeral, otherwise false
     */
    public boolean isEphemeral(String command, Map<String, String> options) {
        Boolean local = localEphemeralRules.get(command);
        if (local != null) return local;
        Boolean global = CommandEphemeral.getEphemeralForCommand(command, options != null ? options : Map.of());
        return global != null && global;
    }

    @Override
    public long getUptimeMillis() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public void setResponseType(ResponseType responseType) {
        this.currentResponseType = responseType;
    }

    @Override
    public ResponseType getCurrentResponseType() {
        return currentResponseType;
    }

    @Override
    public void clearResponseType() {
        this.currentResponseType = null;
    }

    public static class HostComponentRegistry implements ComponentRegistry {
        private final Logger logger;
        private final Map<String, ComponentHandler> buttonHandlers = new ConcurrentHashMap<>();

        public HostComponentRegistry(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void registerButtonHandler(String customId, ComponentHandler handler) {
            buttonHandlers.put(customId, handler);
            logger.info("Registered button handler for: " + customId);
        }

        @Override
        public void unregisterButtonHandler(String customId) {
            buttonHandlers.remove(customId);
            logger.info("Unregistered button handler for: " + customId);
        }

        @Override
        public Button.Builder createButtonBuilder() {
            return new ButtonImpl.Builder();
        }

        public ComponentHandler getButtonHandler(String customId) {
            return buttonHandlers.get(customId);
        }
    }

    private static class HostRoleManager implements RoleManager {
        @Override
        public CompletableFuture<Boolean> addRole(String guildId, String userId, String roleId) {
            return CompletableFuture.completedFuture(true);
        }

        @Override
        public CompletableFuture<Boolean> removeRole(String guildId, String userId, String roleId) {
            return CompletableFuture.completedFuture(true);
        }
    }
} 