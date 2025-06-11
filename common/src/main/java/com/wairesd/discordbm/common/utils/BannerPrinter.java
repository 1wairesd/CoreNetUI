package com.wairesd.discordbm.common.utils;

public final class BannerPrinter {

    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RED    = "\u001B[31m";
    private static final String ANSI_WHITE  = "\u001B[37m";

    public enum Platform {
        BUKKIT, VELOCITY;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static void printBanner(Platform platform) {
        System.out.println(ANSI_PURPLE + " ____    " + ANSI_RED + " __  __ " + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "| __ )  " + ANSI_RED + "|  \\/  |" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "|  _ \\  " + ANSI_RED + "| |\\/| |" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "| |_) | " + ANSI_RED + "| |  | |" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "|____/  " + ANSI_RED + "|_|  |_|" + ANSI_RESET);
        System.out.println();
        System.out.println(ANSI_WHITE + "    DiscordBMV v1.0" + ANSI_RESET);
        System.out.println(ANSI_WHITE + "    Running on " + platform + ANSI_RESET);
        System.out.println();
    }
}
