package com.wairesd.discordbm.host.common.commandbuilder.utils;

public class TimeoutParser {

    public static long parseTimeout(Object timeoutObj) {
        if (timeoutObj == null) {
            throw new IllegalArgumentException("Timeout for button must be specified explicitly (no global default)");
        }

        if (timeoutObj instanceof String str) {
            if (str.equalsIgnoreCase("infinite")) {
                return Long.MAX_VALUE;
            } else {
                try {
                    return Long.parseLong(str) * 60_000;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid timeout format: " + str);
                }
            }
        } else if (timeoutObj instanceof Number number) {
            return number.longValue() * 60_000;
        }

        throw new IllegalArgumentException("Unsupported timeout value: " + timeoutObj);
    }
}
