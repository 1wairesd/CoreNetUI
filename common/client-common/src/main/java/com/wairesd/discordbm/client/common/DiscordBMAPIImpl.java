package com.wairesd.discordbm.client.common;

import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.embed.EmbedBuilder;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.client.common.component.ComponentRegistryImpl;
import com.wairesd.discordbm.common.embed.EmbedBuilderImpl;
import com.wairesd.discordbm.common.logging.LoggerAdapter;
import com.wairesd.discordbm.client.common.message.MessageSenderImpl;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.client.common.role.RoleManagerImpl;
import com.wairesd.discordbm.api.role.RoleManager;
import com.wairesd.discordbm.api.modal.ModalBuilder;
import com.wairesd.discordbm.api.modal.ModalFieldBuilder;
import com.wairesd.discordbm.common.modal.ModalBuilderImpl;
import com.wairesd.discordbm.common.modal.ModalFieldBuilderImpl;
import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.message.ResponseType;
import com.wairesd.discordbm.common.event.EventBusImpl;

public class DiscordBMAPIImpl extends DBMAPI {
    
    private final Platform platform;
    private final MessageSenderImpl messageSender;
    private final ComponentRegistryImpl componentRegistry;
    private final LoggerAdapter logger;
    private final RoleManagerImpl roleManager;
    private final long startTime = System.currentTimeMillis();
    private final EventBus eventBus = new EventBusImpl();
    private ResponseType currentResponseType;

    public DiscordBMAPIImpl(Platform platform, PluginLogger pluginLogger) {
        this.platform = platform;
        this.logger = new LoggerAdapter(pluginLogger);
        this.messageSender = new MessageSenderImpl(platform, this.logger);
        this.componentRegistry = new ComponentRegistryImpl(platform, this.logger);
        this.roleManager = new RoleManagerImpl(platform);
    }
    
    @Override
    public CommandRegistration getCommandRegistration() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return platform.getCommandRegistration();
    }
    
    @Override
    public MessageSender getMessageSender() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return messageSender;
    }
    
    @Override
    public ComponentRegistry getComponentRegistry() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return componentRegistry;
    }
    
    @Override
    public Logger getLogger() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return logger;
    }
    
    @Override
    public EmbedBuilder createEmbedBuilder() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return new EmbedBuilderImpl();
    }
    
    @Override
    public ModalBuilder createModalBuilder() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return new ModalBuilderImpl();
    }
    
    @Override
    public ModalFieldBuilder createModalFieldBuilder() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return new ModalFieldBuilderImpl();
    }
    
    @Override
    public String getServerName() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return platform.getServerName();
    }
    
    @Override
    public boolean isConnected() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return platform.getNettyService() != null && 
               platform.getNettyService().getNettyClient() != null && 
               platform.getNettyService().getNettyClient().isActive();
    }

    @Override
    public RoleManager getRoleManager() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return roleManager;
    }

    @Override
    public long getUptimeMillis() {
        if (startTime == 0) {
            if (logger != null) {
                logger.error("[DiscordBMAPI] startTime is 0 in getUptimeMillis! Throwing NPE.");
            }
            throw new NullPointerException("DiscordBMAPI: startTime is not initialized!");
        }
        return System.currentTimeMillis() - startTime;
    }

    public Platform getPlatform() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return platform;
    }

    @Override
    public EventBus getEventBus() {
        if (eventBus == null) {
            throw new NullPointerException("DiscordBM API: EventBus is not initialized");
        }
        return eventBus;
    }
    
    @Override
    public void setResponseType(ResponseType responseType) {
        this.currentResponseType = responseType;
        this.messageSender.setResponseType(responseType);
    }
    
    @Override
    public ResponseType getCurrentResponseType() {
        return currentResponseType;
    }
    
    @Override
    public void clearResponseType() {
        messageSender.clearResponseType();
    }

    @Override
    public void setEphemeral(boolean ephemeral) {
        messageSender.setEphemeral(ephemeral);
    }

    @Override
    public boolean getCurrentEphemeral() {
        return messageSender.getCurrentEphemeral();
    }

    @Override
    public void clearEphemeral() {
        messageSender.clearEphemeral();
    }
} 