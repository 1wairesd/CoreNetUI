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
import java.util.HashMap;
import java.util.Map;

public class DiscordBMBAPIImpl implements DiscordBMAPI {
    
    private final Platform platform;
    private final MessageSenderImpl messageSender;
    private final ComponentRegistryImpl componentRegistry;
    private final EventRegistryImpl eventRegistry;
    private final LoggerAdapter logger;
    private final RoleManagerImpl roleManager;

    public DiscordBMBAPIImpl(Platform platform, PluginLogger pluginLogger) {
        this.platform = platform;
        this.logger = new LoggerAdapter(pluginLogger);
        this.messageSender = new MessageSenderImpl(platform, this.logger);
        this.componentRegistry = new ComponentRegistryImpl(platform, this.logger);
        this.eventRegistry = new EventRegistryImpl(this.logger);
        this.roleManager = new RoleManagerImpl(platform);
    }
    
    @Override
    public CommandRegistration getCommandRegistration() {
        return platform.getCommandRegistration();
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
    public FormBuilder createFormBuilder() {
        return new FormBuilderImpl();
    }
    
    @Override
    public FormFieldBuilder createFormFieldBuilder() {
        return new FormFieldBuilderImpl();
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

    @Override
    public RoleManager getRoleManager() {
        return roleManager;
    }

    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void registerEphemeralRules(Map<String, Boolean> rules) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "ephemeral_rules");
        msg.put("rules", rules);
        String json = new com.google.gson.Gson().toJson(msg);
        platform.getNettyService().sendNettyMessage(json);
    }
} 