package com.wairesd.discordbm.bukkit.api.models.unregister;

import com.google.gson.Gson;
import com.wairesd.discordbm.bukkit.DiscordBMB;
import com.wairesd.discordbm.bukkit.config.configurators.Settings;
import com.wairesd.discordbm.common.models.unregister.UnregisterMessage;
import com.wairesd.discordbm.common.utils.logging.JavaPluginLogger;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;

import static org.bukkit.Bukkit.getLogger;

public class CommandUnregister {
    private final PluginLogger pluginLogger = new JavaPluginLogger(getLogger());
    private final DiscordBMB plugin;
    private final Gson gson;

    public CommandUnregister(DiscordBMB plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
    }

    public void unregister(String commandName, String pluginName) {
        String secret = Settings.getSecretCode();
        if (secret == null || secret.isEmpty()) return;

        UnregisterMessage msg = new UnregisterMessage(
                plugin.getServerName(),
                pluginName,
                commandName,
                secret
        );
        plugin.getNettyService().sendNettyMessage(gson.toJson(msg));

        if (Settings.isDebugCommandRegistrations()) {
            pluginLogger.info("Sent unregistration message for command: " + commandName);
        }
    }
}
