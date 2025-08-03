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
import com.wairesd.discordbm.api.modal.ModalBuilder;
import com.wairesd.discordbm.api.modal.ModalFieldBuilder;
import com.wairesd.discordbm.common.component.ButtonImpl;
import com.wairesd.discordbm.common.embed.EmbedBuilderImpl;
import com.wairesd.discordbm.common.modal.ModalBuilderImpl;
import com.wairesd.discordbm.common.modal.ModalFieldBuilderImpl;
import com.wairesd.discordbm.common.logging.LoggerAdapter;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.common.event.EventBusImpl;
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
    private Boolean currentEphemeral;

    public HostDiscordBMAPIImpl(CommandRegistration commandRegistration, MessageSender messageSender) {
        this.commandRegistration = commandRegistration;
        this.messageSender = messageSender;
        PluginLogger pluginLogger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
        this.logger = new LoggerAdapter(pluginLogger);
        this.componentRegistry = new HostComponentRegistry(logger);
        this.eventBus = new EventBusImpl(logger);
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
    public ModalBuilder createModalBuilder() {
        return new ModalBuilderImpl();
    }

    @Override
    public ModalFieldBuilder createModalFieldBuilder() {
        return new ModalFieldBuilderImpl();
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
    public long getUptimeMillis() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public void setEphemeral(boolean ephemeral) {
        this.currentEphemeral = ephemeral;
    }

    @Override
    public boolean getCurrentEphemeral() {
        return currentEphemeral != null ? currentEphemeral : false;
    }

    @Override
    public void clearEphemeral() {
        this.currentEphemeral = null;
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

    public static class HostComponentRegistry extends ComponentRegistry {
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

    private static class HostRoleManager extends RoleManager {
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