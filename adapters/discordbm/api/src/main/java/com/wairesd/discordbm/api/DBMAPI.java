package com.wairesd.discordbm.api;

import com.wairesd.discordbm.api.command.CommandRegistration;
import com.wairesd.discordbm.api.embed.EmbedBuilder;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.role.RoleManager;
import com.wairesd.discordbm.api.modal.ModalBuilder;
import com.wairesd.discordbm.api.modal.ModalFieldBuilder;
import com.wairesd.discordbm.api.message.ResponseType;
import lombok.Getter;
import lombok.Setter;

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
     * Create a new embed builder
     * 
     * @return A new embed builder
     */
    public abstract EmbedBuilder createEmbedBuilder();
    
    /**
     * Create a new modal builder
     * 
     * @return A new modal builder
     */
    public abstract ModalBuilder createModalBuilder();
    
    /**
     * Create a new modal field builder
     * 
     * @return A new modal field builder
     */
    public abstract ModalFieldBuilder createModalFieldBuilder();
    
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
     * Returns the uptime of the client in milliseconds.
     */
    public abstract long getUptimeMillis();
    
    /**
     * Set the response type for subsequent message operations.
     * This will affect all sendResponse, sendDirectMessage, sendChannelMessage calls
     * until cleared or changed.
     * 
     * @param responseType The response type to set
     */
    public abstract void setResponseType(ResponseType responseType);
    
    /**
     * Get the currently set response type.
     * 
     * @return The current response type, or null if not set
     */
    public abstract ResponseType getCurrentResponseType();
    
    /**
     * Clear the current response type, reverting to default behavior.
     */
    public abstract void clearResponseType();

    /**
     * Set whether subsequent messages should be ephemeral (private to the user).
     * This will affect all sendResponse, sendDirectMessage, sendChannelMessage calls
     * until cleared or changed.
     * 
     * @param ephemeral true if messages should be ephemeral, false otherwise
     */
    public abstract void setEphemeral(boolean ephemeral);
    
    /**
     * Get the current ephemeral setting.
     * 
     * @return true if ephemeral is set, false otherwise
     */
    public abstract boolean getCurrentEphemeral();
    
    /**
     * Clear the current ephemeral setting, reverting to default behavior.
     */
    public abstract void clearEphemeral();
} 