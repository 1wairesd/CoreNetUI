package com.wairesd.discordbm.api;

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

/**
 * Main interface for the Discord Bot Manager API.
 * This interface provides access to all the core functionality of the Discord Bot Manager.
 */
public interface DiscordBMAPI {
    
    /**
     * Get the command registration service to register Discord commands
     * 
     * @return The command registration service
     */
    CommandRegistration getCommandRegistration();
    
    /**
     * Get the message sender service to send messages to Discord
     * 
     * @return The message sender service
     */
    MessageSender getMessageSender();
    
    /**
     * Get the component registry to register and manage interactive components
     * 
     * @return The component registry
     */
    ComponentRegistry getComponentRegistry();
    
    /**
     * Get the event registry to register event listeners
     * 
     * @return The event registry
     */
    EventRegistry getEventRegistry();
    
    /**
     * Get the event bus for the Discord Bot Manager
     * 
     * @return The event bus
     */
    EventBus getEventBus();
    
    /**
     * Get the logger for the Discord Bot Manager
     * 
     * @return The logger
     */
    Logger getLogger();
    
    /**
     * Create a new embed builder
     * 
     * @return A new embed builder
     */
    EmbedBuilder createEmbedBuilder();
    
    /**
     * Create a new form builder
     * 
     * @return A new form builder
     */
    FormBuilder createFormBuilder();
    
    /**
     * Create a new form field builder
     * 
     * @return A new form field builder
     */
    FormFieldBuilder createFormFieldBuilder();
    
    /**
     * Get the server name
     * 
     * @return The server name
     */
    String getServerName();
    
    /**
     * Check if the connection to Discord is active
     * 
     * @return True if connected, false otherwise
     */
    boolean isConnected();
    
    /**
     * Get the role manager service to manage Discord roles
     *
     * @return The role manager service
     */
    RoleManager getRoleManager();
    
    /**
     * Register ephemeral rules for commands from the addon.
     * These rules will be sent to the host and used for determining message privacy.
     */
    void registerEphemeralRules(Map<String, Boolean> rules);

    /**
     * Returns the uptime of the client in milliseconds.
     */
    long getUptimeMillis();
} 