package com.wairesd.discordbm.host.common.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.error.CommandErrorType;
import org.slf4j.LoggerFactory;

public class Error {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));

    public static CommandErrorType parseErrorType(String errorType) {
        try {
            return CommandErrorType.valueOf(errorType);
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown error type: {}, using SERVER_ERROR", errorType);
            return CommandErrorType.SERVER_ERROR;
        }
    }

    public static void logInvalidUUID(String requestIdStr, IllegalArgumentException e) {
        logger.error("Invalid UUID format for requestId: {}", requestIdStr, e);
    }
}
