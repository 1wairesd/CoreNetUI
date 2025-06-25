package com.wairesd.discordbm.api.role;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing Discord roles for users.
 */
public interface RoleManager {
    /**
     * Adds a role to a user in a guild.
     * @param guildId Discord guild ID
     * @param userId Discord user ID
     * @param roleId Discord role ID
     * @return CompletableFuture with true if successful, false otherwise
     */
    CompletableFuture<Boolean> addRole(String guildId, String userId, String roleId);

    /**
     * Removes a role from a user in a guild.
     * @param guildId Discord guild ID
     * @param userId Discord user ID
     * @param roleId Discord role ID
     * @return CompletableFuture with true if successful, false otherwise
     */
    CompletableFuture<Boolean> removeRole(String guildId, String userId, String roleId);
} 