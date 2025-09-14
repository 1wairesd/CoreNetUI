package com.wairesd.discordbm.api.role;

import java.util.concurrent.CompletableFuture;

/**
 * Abstract class for managing Discord roles for users.
 */
public abstract class RoleManager {
    /**
     * Adds a role to a user in a guild.
     * @param guildId Discord guild ID
     * @param userId Discord user ID
     * @param roleId Discord role ID
     * @return CompletableFuture with true if successful, false otherwise
     */
    public abstract CompletableFuture<Boolean> addRole(String guildId, String userId, String roleId);

    /**
     * Removes a role from a user in a guild.
     * @param guildId Discord guild ID
     * @param userId Discord user ID
     * @param roleId Discord role ID
     * @return CompletableFuture with true if successful, false otherwise
     */
    public abstract CompletableFuture<Boolean> removeRole(String guildId, String userId, String roleId);
} 