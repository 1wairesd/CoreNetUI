package com.wairesd.discordbm.common.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscordBMThreadPool {
    private final ExecutorService executor;

    public DiscordBMThreadPool(int threads) {
        this.executor = Executors.newFixedThreadPool(threads, new DiscordBMThreadFactory());
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static class DiscordBMThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("DiscordBM-Pool-" + count.getAndIncrement());
            return t;
        }
    }
} 