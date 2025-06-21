package com.wairesd.discordbm.common.event;

import com.wairesd.discordbm.api.event.Event;
import com.wairesd.discordbm.api.event.EventListener;
import com.wairesd.discordbm.api.event.EventRegistry;
import com.wairesd.discordbm.api.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRegistryImpl implements EventRegistry {
    
    private final Logger logger;
    private final Map<Class<? extends Event>, List<EventListener<?>>> listeners = new HashMap<>();

    public EventRegistryImpl(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public <T extends Event> void registerListener(EventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }
    
    @Override
    public <T extends Event> void unregisterListener(EventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        if (listeners.containsKey(eventType)) {
            listeners.get(eventType).remove(listener);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void fireEvent(T event) {
        Class<? extends Event> eventType = event.getClass();
        if (listeners.containsKey(eventType)) {
            for (EventListener<?> listener : listeners.get(eventType)) {
                try {
                    ((EventListener<T>) listener).onEvent(event);
                } catch (Exception e) {
                    logger.error("Error firing event to listener", e);
                }
            }
        }
    }
} 