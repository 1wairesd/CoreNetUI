package com.wairesd.discordbm.client.common.service;

import com.wairesd.discordbm.client.common.config.ConfigManager;
import com.wairesd.discordbm.client.common.config.configurators.Messages;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.event.plugin.DiscordBMReloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ClientCommandService {
    private final DBMAPI api;
    private final ConfigManager configManager;
    private final Logger logger;

    public ClientCommandService(DBMAPI api, ConfigManager configManager, Logger logger) {
        this.api = api;
        this.configManager = configManager;
        this.logger = logger;
    }

    public List<String> getHelp() {
        List<String> help = new ArrayList<>();
        help.add(Messages.getMessage(Messages.Keys.HELP_HEADER));
        help.add(Messages.getMessage(Messages.Keys.HELP_RELOAD));
        help.add(Messages.getMessage(Messages.Keys.HELP_INFO));
        return help;
    }

    public List<String> reload() {
        List<String> result = new ArrayList<>();
        configManager.reloadConfigs();
        if (api != null) {
            api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.CONFIG));
            api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.NETWORK));
            api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.COMMANDS));
            api.getEventBus().post(new DiscordBMReloadEvent(DiscordBMReloadEvent.Type.FULL));
            logger.info("Fired reload events");
        }
        result.add(Messages.getMessage(Messages.Keys.RELOAD_SUCCESS));
        return result;
    }
} 