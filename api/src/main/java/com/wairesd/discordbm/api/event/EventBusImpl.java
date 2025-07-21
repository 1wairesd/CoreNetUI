package com.wairesd.discordbm.api.event;

import java.lang.reflect.Method;
import java.util.*;

public class EventBusImpl implements EventBus {
    private final Map<Class<?>, List<ListenerMethod>> listeners = new HashMap<>();

    @Override
    public void register(Subscriber listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1 && Event.class.isAssignableFrom(params[0])) {
                    listeners.computeIfAbsent(params[0], k -> new ArrayList<>())
                        .add(new ListenerMethod(listener, method));
                }
            }
        }
    }

    @Override
    public void unregister(Subscriber listener) {
        for (List<ListenerMethod> list : listeners.values()) {
            list.removeIf(lm -> lm.listener == listener);
        }
    }

    @Override
    public void fireEvent(Event event) {
        List<ListenerMethod> list = listeners.get(event.getClass());
        if (list != null) {
            for (ListenerMethod lm : list) {
                try {
                    lm.method.invoke(lm.listener, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ListenerMethod {
        final Subscriber listener;
        final Method method;
        ListenerMethod(Subscriber listener, Method method) {
            this.listener = listener;
            this.method = method;
        }
    }
} 