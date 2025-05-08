package com.wairesd.discordbm.velocity.commands.commandbuilder.utils;

import com.wairesd.discordbm.velocity.commands.commandbuilder.data.placeholders.PlaceholdersUser;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class EmbedFactoryUtils {
    private static final Logger logger = LoggerFactory.getLogger(EmbedFactoryUtils.class);

    public static MessageEmbed create(Map<String, Object> embedMap, SlashCommandInteractionEvent event, Context context) {
        EmbedBuilder builder = new EmbedBuilder();

        if (embedMap.containsKey("title")) {
            builder.setTitle(format((String) embedMap.get("title"), event, context));
        }

        if (embedMap.containsKey("description")) {
            builder.setDescription(format((String) embedMap.get("description"), event, context));
        }

        if (embedMap.containsKey("color")) {
            try {
                int color = parseColor(embedMap.get("color"));
                builder.setColor(color);
            } catch (NumberFormatException e) {
                logger.warn("Invalid color format: {}", embedMap.get("color"));
            }
        }

        if (embedMap.containsKey("fields")) {
            List<Map<String, Object>> fields = (List<Map<String, Object>>) embedMap.get("fields");
            for (Map<String, Object> field : fields) {
                String name = format((String) field.get("name"), event, context);
                String value = format((String) field.get("value"), event, context);
                boolean inline = (Boolean) field.getOrDefault("inline", false);
                builder.addField(name, value, inline);
            }
        }

        if (embedMap.containsKey("author")) {
            Map<String, Object> author = (Map<String, Object>) embedMap.get("author");
            String name = format((String) author.get("name"), event, context);
            String url = getSafeUrl(author.get("url"), event, context);
            String icon = getSafeUrl(author.get("icon_url"), event, context);
            builder.setAuthor(name, url, icon);
        }

        if (embedMap.containsKey("footer")) {
            Map<String, Object> footer = (Map<String, Object>) embedMap.get("footer");
            String text = format((String) footer.get("text"), event, context);
            String icon = getSafeUrl(footer.get("icon_url"), event, context);
            builder.setFooter(text, icon);
        }

        if (embedMap.containsKey("thumbnail")) {
            String thumb = format((String) embedMap.get("thumbnail"), event, context);
            if (isValidUrl(thumb)) builder.setThumbnail(thumb);
        }

        if (embedMap.containsKey("image")) {
            String image = format((String) embedMap.get("image"), event, context);
            if (isValidUrl(image)) builder.setImage(image);
        }

        return builder.build();
    }

    private static String format(String template, SlashCommandInteractionEvent event, Context context) {
        String result = PlaceholdersUser.replace(template != null ? template : "", event, context);
        for (OptionMapping option : event.getOptions()) {
            result = result.replace("{" + option.getName() + "}", option.getAsString());
        }
        return result;
    }

    private static boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private static String getSafeUrl(Object raw, SlashCommandInteractionEvent event, Context context) {
        if (raw == null) return null;
        String formatted = format(raw.toString(), event, context);
        return isValidUrl(formatted) ? formatted : null;
    }

    private static int parseColor(Object color) throws NumberFormatException {
        if (color instanceof Integer) return (Integer) color;
        String str = color.toString().trim();
        if (str.startsWith("#")) str = str.substring(1);
        return Integer.parseInt(str, 16);
    }
}
