package com.wairesd.discordbm.api.event;

public interface EventBus {
    void register(Subscriber listener);
    void unregister(Subscriber listener);
    void fireEvent(DBMEvent event);
} 