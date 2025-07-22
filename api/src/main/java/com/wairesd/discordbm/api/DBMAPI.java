package com.wairesd.discordbm.api;

import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.embed.EmbedBuilder;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.api.role.RoleManager;
import com.wairesd.discordbm.api.form.FormBuilder;
import com.wairesd.discordbm.api.form.FormFieldBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Main abstract class for the Discord Bot Manager API.
 * This class provides access to all the core functionality of the Discord Bot Manager.
 */
public abstract class DBMAPI {
    /**
     * Singleton instance of the API.
     * -- GETTER --
     *  Returns the singleton instance of the API.
     * -- SETTER --
     *  Sets the singleton instance of the API.


     */
    @Setter
    @Getter
    private static DBMAPI instance;

    /**
     * Get the command registration service to register Discord commands
     * 
     * @return The command registration service
     */
    public abstract CommandRegistration getCommandRegistration();
    
    /**
     * Get the message sender service to send messages to Discord
     * 
     * @return The message sender service
     */
    public abstract MessageSender getMessageSender();
    
    /**
     * Get the component registry to register and manage interactive components
     * 
     * @return The component registry
     */
    public abstract ComponentRegistry getComponentRegistry();
    
    /**
     * Get the event bus for the Discord Bot Manager
     * 
     * @return The event bus
     */
    public abstract EventBus getEventBus();
    
    /**
     * Get the logger for the Discord Bot Manager
     * 
     * @return The logger
     */
    public abstract Logger getLogger();
    
    /**
     * Create a new embed builder
     * 
     * @return A new embed builder
     */
    public abstract EmbedBuilder createEmbedBuilder();
    
    /**
     * Create a new form builder
     * 
     * @return A new form builder
     */
    public abstract FormBuilder createFormBuilder();
    
    /**
     * Create a new form field builder
     * 
     * @return A new form field builder
     */
    public abstract FormFieldBuilder createFormFieldBuilder();
    
    /**
     * Get the server name
     * 
     * @return The server name
     */
    public abstract String getServerName();
    
    /**
     * Check if the connection to Discord is active
     * 
     * @return True if connected, false otherwise
     */
    public abstract boolean isConnected();
    
    /**
     * Get the role manager service to manage Discord roles
     *
     * @return The role manager service
     */
    public abstract RoleManager getRoleManager();
    
    /**
     * Register ephemeral rules for commands from the addon.
     * These rules will be sent to the host and used for determining message privacy.
     */
    public abstract void registerEphemeralRules(Map<String, Boolean> rules);

    /**
     * Returns the uptime of the client in milliseconds.
     */
    public abstract long getUptimeMillis();
} 