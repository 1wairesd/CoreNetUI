package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons;

import com.wairesd.discordbm.velocity.config.configurators.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ButtonActionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ButtonActionRegistry.class);

    private static class ButtonData {
        private final String message;
        private final long expirationTime;

        public ButtonData(String message, long expirationTime) {
            this.message = message;
            this.expirationTime = expirationTime;
        }

        public String getMessage() {
            return message;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expirationTime;
        }
    }

    public static class FormButtonData {
        private final String formName;
        private final String messageTemplate;
        private final String requiredRoleId;
        private final long expirationTime;

        public FormButtonData(String formName, String messageTemplate, String requiredRoleId, long expirationTime) {
            this.formName = formName;
            this.messageTemplate = messageTemplate;
            this.requiredRoleId = requiredRoleId;
            this.expirationTime = expirationTime;
        }

        public String getFormName() {
            return formName;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public String getRequiredRoleId() {
            return requiredRoleId;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expirationTime;
        }
    }

    private static final ConcurrentMap<String, ButtonData> buttonDataMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, FormButtonData> formButtonDataMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        scheduler.scheduleAtFixedRate(ButtonActionRegistry::cleanupExpiredEntries, 1, 1, TimeUnit.MINUTES);
    }

    public static void register(String id, String message, long durationMillis) {
        if (id == null || id.isEmpty() || message == null) {
            throw new IllegalArgumentException("ID and message cannot be null or empty");
        }
        long expirationTime = System.currentTimeMillis() + durationMillis;
        if (expirationTime < 0) {
            expirationTime = Long.MAX_VALUE;
        }

        buttonDataMap.put(id, new ButtonData(message, expirationTime));

        if (Settings.isDebugButtonRegister()) {
            logger.info("Registered button: id={}, expirationTime={}", id, expirationTime);
        }
    }

    public static void registerFormButton(String id, String formName, String messageTemplate, String requiredRoleId, long durationMillis) {
        if (id == null || id.isEmpty() || formName == null || formName.isEmpty()) {
            throw new IllegalArgumentException("ID and form_name cannot be null or empty");
        }
        long expirationTime = System.currentTimeMillis() + durationMillis;
        if (expirationTime < 0) {
            expirationTime = Long.MAX_VALUE;
        }

        formButtonDataMap.put(id, new FormButtonData(formName, messageTemplate, requiredRoleId, expirationTime));

        if (Settings.isDebugButtonRegister()) {
            logger.info("Registered form button: id={}, formName={}, requiredRoleId={}, expirationTime={}, totalStored={}",
                    id, formName, requiredRoleId, expirationTime, formButtonDataMap.size());
        }
    }

    public static String getMessage(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        ButtonData data = buttonDataMap.remove(id);
        return (data != null && !data.isExpired()) ? data.getMessage() : null;
    }

    public static FormButtonData getFormButtonData(String id) {
        boolean debugButtonData = Settings.isDebugButtonData();

        if (id == null || id.isEmpty()) {
            return null;
        }

        if (debugButtonData) {
            logger.info("Getting form button: id={}, mapSize={}", id, formButtonDataMap.size());
        }

        FormButtonData data = formButtonDataMap.remove(id);

        if (data == null) {
            if (debugButtonData) {
                logger.warn("No FormButtonData found for id={}", id);
            }
        } else if (data.isExpired()) {
            if (debugButtonData) {
                logger.warn("FormButtonData for id={} is expired", id);
            }
        } else {
            if (debugButtonData) {
                logger.info("Found valid FormButtonData for id={}", id);
            }
        }

        return (data != null && !data.isExpired()) ? data : null;
    }

    private static void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        buttonDataMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
        formButtonDataMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public static void shutdown() {
        scheduler.shutdown();
    }
}