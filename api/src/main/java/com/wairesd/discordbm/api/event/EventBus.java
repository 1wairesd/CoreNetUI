package com.wairesd.discordbm.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private static final List<DCEventHandler<?>> handlers = new CopyOnWriteArrayList<>();

    public static void register(@NotNull Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public static void unregister(@NotNull Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public static void post(@NotNull DiscordBMEvent event) {
        for (DCEventHandler<?> handler : handlers) {
            try {
                handler.handle(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <T extends DiscordBMEvent> void registerHandler(@NotNull DCEventHandler<T> handler) {
        handlers.add(handler);
    }

    public static <T extends DiscordBMEvent> void unregisterHandler(@NotNull DCEventHandler<T> handler) {
        handlers.remove(handler);
    }

    @FunctionalInterface
    public interface DCEventHandler<T extends DiscordBMEvent> {
        void handle(DiscordBMEvent event);
    }
}
