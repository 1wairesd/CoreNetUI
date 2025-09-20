package com.wairesd.discordbm.addons.dbmdonatecase.listener;

import com.wairesd.discordbm.api.event.Subscriber;
import com.wairesd.discordbm.api.event.plugin.DiscordBMReloadEvent;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import net.kyori.event.PostOrders;
import net.kyori.event.method.annotation.PostOrder;
import net.kyori.event.method.annotation.Subscribe;

public class DBMReloadListener implements Subscriber {
    private final Messages messages;

    public DBMReloadListener(Messages messages) {
        this.messages = messages;
    }

    @Subscribe
    @PostOrder(PostOrders.LAST)
    public void onEvent(DiscordBMReloadEvent event) {
        messages.reload();
    }
} 