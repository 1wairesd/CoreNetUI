package com.wairesd.discordbm.common.utils.color;

import com.wairesd.discordbm.common.utils.color.transform.AnsiColorTranslator;
import com.wairesd.discordbm.common.utils.color.transform.BukkitColorTranslator;
import net.kyori.adventure.text.Component;

public class UniversalColorTranslator {
    public static String translate(String message, MessageContext context) {
        if (message == null) return "";
        switch (context) {
            case CONSOLE:
                return AnsiColorTranslator.translate(message);
            case CHAT:
                return BukkitColorTranslator.translate(message);
            case DISCORD:
                return DiscordColorTranslator.translate(message);
            case LOG:
            default:
                return PlainTextTranslator.translate(message);
        }
    }

    public static Component translateComponent(String message, MessageContext context) {
        if (context == MessageContext.CHAT) {
            return ColorUtils.parseComponent(message);
        } else {
            return Component.text(translate(message, context));
        }
    }
} 