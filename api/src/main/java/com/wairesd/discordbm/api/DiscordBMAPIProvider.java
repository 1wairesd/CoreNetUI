package com.wairesd.discordbm.api;

public class DiscordBMAPIProvider {
    private static DiscordBMAPI instance;

    public static DiscordBMAPI getInstance() {
        return instance;
    }

    public static void setInstance(DiscordBMAPI api) {
        instance = api;
    }

    /**
     * Returns the current instance of DiscordBMAPI or throws if not initialized.
     */
    public static DiscordBMAPI getInstanceOrThrow() {
        if (instance == null) {
            throw new IllegalStateException("DiscordBMAPI is not initialized!");
        }
        return instance;
    }
} 