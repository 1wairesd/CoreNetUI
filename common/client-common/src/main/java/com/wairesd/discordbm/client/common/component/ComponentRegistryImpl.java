package com.wairesd.discordbm.client.common.component;

import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.component.ComponentHandler;
import com.wairesd.discordbm.api.component.ComponentRegistry;
import com.wairesd.discordbm.api.logging.Logger;
import com.wairesd.discordbm.client.common.platform.Platform;
import com.wairesd.discordbm.common.component.ButtonImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentRegistryImpl extends ComponentRegistry {
    
    private final Platform platform;
    private final Logger logger;
    private final Map<String, ComponentHandler> buttonHandlers = new ConcurrentHashMap<>();

    public ComponentRegistryImpl(Platform platform, Logger logger) {
        this.platform = platform;
        this.logger = logger;
    }
    
    @Override
    public void registerButtonHandler(String customId, ComponentHandler handler) {
        buttonHandlers.put(customId, handler);
    }
    
    @Override
    public void unregisterButtonHandler(String customId) {
        buttonHandlers.remove(customId);
    }
    
    @Override
    public Button.Builder createButtonBuilder() {
        return new ButtonImpl.Builder();
    }
} 