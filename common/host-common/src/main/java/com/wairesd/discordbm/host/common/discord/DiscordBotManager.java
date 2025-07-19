package com.wairesd.discordbm.host.common.discord;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.activity.ActivityFactory;
import com.wairesd.discordbm.host.common.discord.activity.ActivityUpdater;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class DiscordBotManager {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private JDA jda;
    private boolean initialized = false;

    public DiscordBotManager() {
    }

    public void initializeBot(String token, String activityType, String activityMessage) {
        if (token == null || token.isEmpty() || "your-bot-token".equals(token)) {
            logger.error("❌ Bot token is not specified or invalid!");
            logger.error("Please set a valid bot token in settings.yml under Discord.Bot-token");
            return;
        }
        if (initialized) {
            logger.warn("Bot is already initialized!");
            return;
        }

        try {
            ActivityFactory activityFactory = new ActivityFactory();
            Activity activity = activityFactory.createActivity(activityType, activityMessage);

            jda = JDABuilder.createDefault(token)
                    .enableIntents(EnumSet.of(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_MEMBERS
                    ))
                    .setActivity(activity)
                    .build()
                    .awaitReady();

            logger.info("Discord bot initialized successfully");
            initialized = true;
        } catch (InvalidTokenException e) {
            logger.error("Invalid bot token provided!");
            logger.error("Please check your bot token in settings.yml and make sure it's correct");
            jda = null;
            initialized = false;
        } catch (Exception e) {
            logger.error("Error initializing Discord bot: {}", e.getMessage());
            jda = null;
            initialized = false;
        }
    }

    public void updateActivity(String activityType, String activityMessage) {
        if (!initialized || jda == null) {
            logger.warn("JDA is not initialized — cannot update activity");
            return;
        }
        ActivityFactory activityFactory = new ActivityFactory();
        ActivityUpdater activityUpdater = new ActivityUpdater(jda, activityFactory);
        activityUpdater.updateActivity(activityType, activityMessage);
    }

    public JDA getJda() {
        if (!initialized || jda == null) {
            logger.warn("JDA is not initialized yet!");
            return null;
        }
        return jda;
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }
}
