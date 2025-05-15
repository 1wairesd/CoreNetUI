package com.wairesd.discordbm.velocity.discord.activity;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;

public class ActivityUpdater {
    private final JDA jda;
    private final ActivityFactory activityFactory;
    private final Logger logger;

    public ActivityUpdater(JDA jda, ActivityFactory activityFactory, Logger logger) {
        this.jda = jda;
        this.activityFactory = activityFactory;
        this.logger = logger;
    }

    public void updateActivity(String activityType, String activityMessage) {
        if (jda != null) {
            Activity activity = activityFactory.createActivity(activityType, activityMessage);
            jda.getPresence().setActivity(activity);
            logger.info("Bot activity updated to: {} {}", activityType, activityMessage);
        } else {
            logger.warn("Cannot update activity — JDA not initialized");
        }
    }
}