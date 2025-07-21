package com.wairesd.discordbm.addons.dbmdonatecase.listener;

import com.wairesd.discordbm.api.event.EventListener;
import com.wairesd.discordbm.api.event.Subscriber;
import com.wairesd.discordbm.api.event.plugin.DiscordBMReloadEvent;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;

public class DBMReloadListener implements Subscriber, EventListener<DiscordBMReloadEvent> {
    private final Messages messages;

    public DBMReloadListener(Messages messages) {
        this.messages = messages;
    }

    @Override
    public void onEvent(DiscordBMReloadEvent event) {
        messages.reload();
    }

    @Override
    public Class<DiscordBMReloadEvent> getEventType() {
        return DiscordBMReloadEvent.class;
    }
} 