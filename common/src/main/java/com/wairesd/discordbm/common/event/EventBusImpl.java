package com.wairesd.discordbm.common.event;

import com.wairesd.discordbm.api.event.DBMEvent;
import com.wairesd.discordbm.api.event.EventBus;
import com.wairesd.discordbm.api.event.Subscriber;
import com.wairesd.discordbm.api.logging.Logger;
import net.kyori.event.EventSubscriber;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodHandleEventExecutorFactory;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import com.google.common.collect.SetMultimap;

public class EventBusImpl implements EventBus {
    private final Logger logger;
    private final SimpleEventBus<DBMEvent> bus = new SimpleEventBus<>(DBMEvent.class);
    private final MethodSubscriptionAdapter<Subscriber> methodAdapter = new SimpleMethodSubscriptionAdapter<>(bus, new MethodHandleEventExecutorFactory<>());

    public EventBusImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public @NonNull Class<DBMEvent> eventType() {
        return bus.eventType();
    }

    @Override
    public @NonNull PostResult post(@NonNull DBMEvent event) {
        PostResult postResult = bus.post(event);

        if (!postResult.wasSuccessful()) {
            logger.warn("Failed to post event: " + event.getClass().getSimpleName());

            postResult.exceptions().forEach((subscriber, exception) ->
                    logger.warn("Subscriber: " + subscriber, exception)
            );
        }

        return postResult;
    }

    @Override
    public void register(@NotNull Subscriber listener) {
        try {
            methodAdapter.register(listener);
        } catch (Exception e) {
            logger.warn("Error with event listener " + listener.getClass() + " registration:", e);
        }
    }

    @Override
    public <T extends DBMEvent> void register(@NonNull Class<T> clazz, @NonNull EventSubscriber<? super T> subscriber) {
        try {
            bus.register(clazz, subscriber);
        } catch (Exception e) {
            logger.warn("Error with event subscriber " + subscriber.getClass() + " registration:", e);
        }
    }

    @Override
    public void unregister(@NotNull Subscriber listener) {
        methodAdapter.unregister(listener);
    }

    @Override
    public void unregister(@NonNull EventSubscriber<?> subscriber) {
        bus.unregister(subscriber);
    }

    @Override
    public void unregister(@NonNull Predicate<EventSubscriber<?>> predicate) {
        bus.unregister(predicate);
    }

    @Override
    public void unregisterAll() {
        bus.unregisterAll();
    }

    @Override
    public <T extends DBMEvent> boolean hasSubscribers(@NonNull Class<T> clazz) {
        return bus.hasSubscribers(clazz);
    }

    @Override
    public @NonNull SetMultimap<Class<?>, EventSubscriber<?>> subscribers() {
        return bus.subscribers();
    }
} 