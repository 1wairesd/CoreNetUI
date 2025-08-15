package com.wairesd.discordbm.host.common.handler;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.config.configurators.Settings;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class DiscordMessageHandler {

    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static final String DISCORD_MESSAGE_CHANNEL = "discord:message";

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (isDiscordMessageChannel(event)) {
            processDiscordMessage(event);
        }
    }

    private boolean isDiscordMessageChannel(PluginMessageEvent event) {
        return DISCORD_MESSAGE_CHANNEL.equals(event.getIdentifier().getId());
    }

    private void processDiscordMessage(PluginMessageEvent event) {
        String message = extractMessageFromEvent(event);
        logMessageIfDebugEnabled(message);
        markEventAsHandled(event);
    }

    private String extractMessageFromEvent(PluginMessageEvent event) {
        return new String(event.getData(), StandardCharsets.UTF_8);
    }

    private void logMessageIfDebugEnabled(String message) {
        if (Settings.isDebugPluginConnections()) {
            logger.info("Received message from Bukkit plugin: {}", message);
        }
    }

    private void markEventAsHandled(PluginMessageEvent event) {
        event.setResult(PluginMessageEvent.ForwardResult.handled());
    }
}