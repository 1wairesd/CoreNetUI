package com.wairesd.discordbm.client.common;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.embed.EmbedBuilder;
import com.wairesd.discordbm.api.event.EventRegistry;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.client.common.command.CommandRegistrationImpl;
import com.wairesd.discordbm.client.common.component.ComponentRegistryImpl;
import com.wairesd.discordbm.client.common.embed.EmbedBuilderImpl;
import com.wairesd.discordbm.client.common.event.EventRegistryImpl;
import com.wairesd.discordbm.client.common.logging.LoggerAdapter;
import com.wairesd.discordbm.client.common.message.MessageSenderImpl;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;

public class DiscordBMAPIImpl implements DiscordBMAPI {
    
    private final Platform platform;
    private final CommandRegistrationImpl commandRegistration;
    private final MessageSenderImpl messageSender;
    private final ComponentRegistryImpl componentRegistry;
    private final EventRegistryImpl eventRegistry;
    private final LoggerAdapter logger;

    public DiscordBMAPIImpl(Platform platform, PluginLogger pluginLogger) {
        this.platform = platform;
        this.logger = new LoggerAdapter(pluginLogger);
        this.commandRegistration = new CommandRegistrationImpl(platform, this.logger);
        this.messageSender = new MessageSenderImpl(platform, this.logger);
        this.componentRegistry = new ComponentRegistryImpl(platform, this.logger);
        this.eventRegistry = new EventRegistryImpl(this.logger);
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
    public EventRegistry getEventRegistry() {
        return eventRegistry;
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
    public String getServerName() {
        return platform.getServerName();
    }
    
    @Override
    public boolean isConnected() {
        return platform.getNettyService() != null && 
               platform.getNettyService().getNettyClient() != null && 
               platform.getNettyService().getNettyClient().isActive();
    }

    public Platform getPlatform() {
        return platform;
    }
} 