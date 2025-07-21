package com.wairesd.discordbm.host.common.api;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.embed.EmbedBuilder;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.event.EventRegistry;
import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.role.RoleManager;
import com.wairesd.discordbm.api.form.FormBuilder;
import com.wairesd.discordbm.api.form.FormFieldBuilder;

import java.util.Map;

public class HostDiscordBMAPIImpl implements DiscordBMAPI {
    private final CommandRegistration commandRegistration;
    private final MessageSender messageSender;

    public HostDiscordBMAPIImpl(CommandRegistration commandRegistration, MessageSender messageSender) {
        this.commandRegistration = commandRegistration;
        this.messageSender = messageSender;
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
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public EventRegistry getEventRegistry() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public EventBus getEventBus() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Logger getLogger() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public EmbedBuilder createEmbedBuilder() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public FormBuilder createFormBuilder() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public FormFieldBuilder createFormFieldBuilder() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public RoleManager getRoleManager() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void registerEphemeralRules(Map<String, Boolean> rules) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long getUptimeMillis() {
        throw new UnsupportedOperationException("Not implemented");
    }
} 