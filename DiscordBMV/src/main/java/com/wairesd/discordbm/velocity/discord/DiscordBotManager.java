package com.wairesd.discordbm.velocity.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;

import java.util.EnumSet;

public class DiscordBotManager {
    private final Logger logger;
    private JDA jda;
    private boolean initialized = false;

    public DiscordBotManager(Logger logger) {
        this.logger = logger;
    }

    public void initializeBot(String token, String activityType, String activityMessage) {
        if (token == null || token.isEmpty()) {
            logger.error("Bot token is not specified!");
            return;
        }
        if (initialized) {
            logger.warn("Bot is already initialized!");
            return;
        }
        try {
            Activity activity = createActivity(activityType, activityMessage);
            jda = JDABuilder.createDefault(token)
                    .enableIntents(EnumSet.of(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT
                    ))
                    .setActivity(activity)
                    .build()
                    .awaitReady();
            logger.info("JDA initialized successfully with token: {}", token.substring(0, 10) + "...");
            initialized = true;
        } catch (Exception e) {
            logger.error("Error initializing JDA: {}", e.getMessage(), e);
            jda = null;
            initialized = false;
        }
    }

    private Activity createActivity(String activityType, String activityMessage) {
        return switch (activityType.toLowerCase()) {
            case "playing"   -> Activity.playing(activityMessage);
            case "watching"  -> Activity.watching(activityMessage);
            case "listening" -> Activity.listening(activityMessage);
            case "competing" -> Activity.competing(activityMessage);
            default           -> Activity.playing(activityMessage);
        };
    }

    public void updateActivity(String activityType, String activityMessage) {
        if (jda != null) {
            jda.getPresence().setActivity(createActivity(activityType, activityMessage));
            logger.info("Bot activity updated to: {} {}", activityType, activityMessage);
        } else {
            logger.warn("Cannot update activity — JDA not initialized");
        }
    }

    public JDA getJda() {
        if (!initialized || jda == null) {
            logger.warn("JDA is not initialized yet!");
            return null;
        }
        return jda;
    }
}
