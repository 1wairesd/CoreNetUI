package com.wairesd.discordbm.client.common;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.embed.EmbedBuilder;
import com.wairesd.discordbm.api.event.EventRegistry;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.client.common.component.ComponentRegistryImpl;
import com.wairesd.discordbm.client.common.embed.EmbedBuilderImpl;
import com.wairesd.discordbm.client.common.event.EventRegistryImpl;
import com.wairesd.discordbm.client.common.logging.LoggerAdapter;
import com.wairesd.discordbm.client.common.message.MessageSenderImpl;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.client.common.role.RoleManagerImpl;
import com.wairesd.discordbm.api.role.RoleManager;
import com.wairesd.discordbm.api.form.FormBuilder;
import com.wairesd.discordbm.api.form.FormFieldBuilder;
import com.wairesd.discordbm.client.common.form.FormBuilderImpl;
import com.wairesd.discordbm.client.common.form.FormFieldBuilderImpl;
import com.wairesd.discordbm.client.common.ephemeral.EphemeralRulesManager;

import java.util.Map;

public class DiscordBMBAPIImpl implements DiscordBMAPI {
    
    private final Platform platform;
    private final MessageSenderImpl messageSender;
    private final ComponentRegistryImpl componentRegistry;
    private final EventRegistryImpl eventRegistry;
    private final LoggerAdapter logger;
    private final RoleManagerImpl roleManager;
    private final EphemeralRulesManager ephemeralRulesManager;

    public DiscordBMBAPIImpl(Platform platform, PluginLogger pluginLogger) {
        this.platform = platform;
        this.logger = new LoggerAdapter(pluginLogger);
        this.messageSender = new MessageSenderImpl(platform, this.logger);
        this.componentRegistry = new ComponentRegistryImpl(platform, this.logger);
        this.eventRegistry = new EventRegistryImpl(this.logger);
        this.roleManager = new RoleManagerImpl(platform);
        this.ephemeralRulesManager = new EphemeralRulesManager(platform);
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
    public EventRegistry getEventRegistry() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return eventRegistry;
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
    public FormBuilder createFormBuilder() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return new FormBuilderImpl();
    }
    
    @Override
    public FormFieldBuilder createFormFieldBuilder() {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        return new FormFieldBuilderImpl();
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
    public void registerEphemeralRules(Map<String, Boolean> rules) {
        if (platform == null) {
            throw new NullPointerException("DiscordBM API: Platform is not initialized");
        }
        ephemeralRulesManager.registerEphemeralRules(rules);
    }

    public Platform getPlatform() {
        return platform;
    }

    public EphemeralRulesManager getEphemeralRulesManager() {
        return ephemeralRulesManager;
    }
} 