package com.wairesd.discordbm.common.event;

import com.wairesd.discordbm.api.event.DBMEvent;
import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.event.Subscriber;
import net.kyori.event.EventSubscriber;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodHandleEventExecutorFactory;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EventBusImpl implements EventBus {
    private final SimpleEventBus<DBMEvent> bus = new SimpleEventBus<>(DBMEvent.class);
    private final MethodSubscriptionAdapter<Subscriber> methodAdapter = new SimpleMethodSubscriptionAdapter<>(bus, new MethodHandleEventExecutorFactory<>());

    @Override
    public void register(@NotNull Subscriber listener) {
        try {
            methodAdapter.register(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregister(@NotNull Subscriber listener) {
        methodAdapter.unregister(listener);
    }

    @Override
    public void fireEvent(DBMEvent event) {
        bus.post(event);
    }

    public <T extends DBMEvent> void register(@NotNull Class<T> clazz, @NotNull EventSubscriber<? super T> subscriber) {
        bus.register(clazz, subscriber);
    }

    public void unregister(@NotNull EventSubscriber<?> subscriber) {
        bus.unregister(subscriber);
    }

    public void unregister(@NotNull Predicate<EventSubscriber<?>> predicate) {
        bus.unregister(predicate);
    }

    public void unregisterAll() {
        bus.unregisterAll();
    }
} 