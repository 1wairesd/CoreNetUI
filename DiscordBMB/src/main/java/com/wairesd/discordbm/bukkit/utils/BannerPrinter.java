package com.wairesd.discordbm.bukkit.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;

public class BannerPrinter {

    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RED    = "\u001B[31m";
    private static final String ANSI_WHITE  = "\u001B[37m";

    public static void printBanner(PluginLogger logger) {
        logger.info(ANSI_PURPLE + " ____    " + ANSI_RED + " __  __ " + ANSI_RESET);
        logger.info(ANSI_PURPLE + "| __ )  " + ANSI_RED + "|  \\/  |" + ANSI_RESET);
        logger.info(ANSI_PURPLE + "|  _ \\  " + ANSI_RED + "| |\\/| |" + ANSI_RESET);
        logger.info(ANSI_PURPLE + "| |_) | " + ANSI_RED + "| |  | |" + ANSI_RESET);
        logger.info(ANSI_PURPLE + "|____/  " + ANSI_RED + "|_|  |_|" + ANSI_RESET);
        logger.info("");
        logger.info(ANSI_WHITE + "    DiscordBMV v1.0" + ANSI_RESET);
        logger.info(ANSI_WHITE + "    Running on Velocity" + ANSI_RESET);
        logger.info("");
    }
}
