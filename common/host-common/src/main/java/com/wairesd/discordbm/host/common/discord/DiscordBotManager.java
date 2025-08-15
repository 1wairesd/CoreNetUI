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
    private static final String DEFAULT_INVALID_TOKEN = "your-bot-token";

    private final ActivityFactory activityFactory;
    private JDA jda;
    private boolean initialized = false;

    public DiscordBotManager() {
        this.activityFactory = new ActivityFactory();
    }

    public void initializeBot(String token, String activityType, String activityMessage) {
        if (!isValidToken(token)) {
            logInvalidToken();
            return;
        }

        if (isAlreadyInitialized()) {
            return;
        }

        try {
            Activity activity = activityFactory.createActivity(activityType, activityMessage);
            jda = buildJda(token, activity);
            initialized = true;
        } catch (InvalidTokenException e) {
            handleInvalidTokenException();
        } catch (Exception e) {
            handleInitializationException(e);
        }
    }

    public void updateActivity(String activityType, String activityMessage) {
        if (!isJdaReady()) {
            return;
        }

        ActivityUpdater activityUpdater = new ActivityUpdater(jda, activityFactory);
        activityUpdater.updateActivity(activityType, activityMessage);
    }

    public JDA getJda() {
        if (!isJdaReady()) {
            return null;
        }
        return jda;
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty() && !DEFAULT_INVALID_TOKEN.equals(token);
    }

    private void logInvalidToken() {
        logger.error("❌ Bot token is not specified or invalid!");
        logger.error("Please set a valid bot token in settings.yml under Discord.Bot-token");
    }

    private boolean isAlreadyInitialized() {
        if (initialized) {
            logger.warn("Bot is already initialized!");
            return true;
        }
        return false;
    }

    private JDA buildJda(String token, Activity activity) throws Exception {
        return JDABuilder.createDefault(token)
                .enableIntents(getRequiredIntents())
                .setActivity(activity)
                .build()
                .awaitReady();
    }

    private EnumSet<GatewayIntent> getRequiredIntents() {
        return EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS
        );
    }

    private void handleInvalidTokenException() {
        logger.error("Invalid bot token provided!");
        logger.error("Please check your bot token in settings.yml and make sure it's correct");
        resetJdaState();
    }

    private void handleInitializationException(Exception e) {
        logger.error("Error initializing Discord bot: {}", e.getMessage());
        resetJdaState();
    }

    private void resetJdaState() {
        jda = null;
        initialized = false;
    }

    private boolean isJdaReady() {
        if (!initialized || jda == null) {
            logger.warn("JDA is not initialized — cannot update activity");
            return false;
        }
        return true;
    }
}