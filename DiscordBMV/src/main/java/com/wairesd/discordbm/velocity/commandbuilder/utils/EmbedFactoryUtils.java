package com.wairesd.discordbm.velocity.commandbuilder.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.velocity.commandbuilder.models.context.Context;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.Interaction;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class EmbedFactoryUtils {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));

    public static CompletableFuture<MessageEmbed> create(Map<String, Object> embedMap, Interaction event, Context context) {
        EmbedBuilder builder = new EmbedBuilder();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        processString(embedMap, "title", event, context, futures, builder::setTitle);
        processString(embedMap, "description", event, context, futures, builder::setDescription);
        processUrl(embedMap, "thumbnail", event, context, futures, builder::setThumbnail);
        processUrl(embedMap, "image", event, context, futures, builder::setImage);

        if (embedMap.containsKey("color")) {
            try {
                builder.setColor(parseColor(embedMap.get("color")));
            } catch (NumberFormatException e) {
                logger.warn("Invalid color format: {}", embedMap.get("color"));
            }
        }

        if (embedMap.get("author") instanceof Map) {
            Map<String, Object> authorMap = (Map<String, Object>) embedMap.get("author");
            processAuthor(authorMap, event, context, futures, builder);
        } else if (embedMap.get("author") instanceof String) {
            processString(embedMap, "author", event, context, futures, (name) -> builder.setAuthor(name, null, null));
        }

        if (embedMap.get("footer") instanceof Map) {
            Map<String, Object> footerMap = (Map<String, Object>) embedMap.get("footer");
            processFooter(footerMap, event, context, futures, builder);
        } else if (embedMap.get("footer") instanceof String) {
            processString(embedMap, "footer", event, context, futures, (text) -> builder.setFooter(text, null));
        }

        if (embedMap.get("fields") instanceof List) {
            List<Map<String, Object>> fields = (List<Map<String, Object>>) embedMap.get("fields");
            processFields(fields, event, context, futures, builder);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> builder.build());
    }

    private static void processString(Map<String, Object> map, String key, Interaction event, Context context, List<CompletableFuture<?>> futures, Consumer<String> consumer) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value != null) {
                futures.add(MessageFormatterUtils.format(value.toString(), event, context, false).thenAccept(consumer));
            }
        }
    }

    private static void processUrl(Map<String, Object> map, String key, Interaction event, Context context, List<CompletableFuture<?>> futures, Consumer<String> consumer) {
        processString(map, key, event, context, futures, url -> {
            if (isValidUrl(url)) {
                consumer.accept(url);
            }
        });
    }

    private static void processAuthor(Map<String, Object> authorMap, Interaction event, Context context, List<CompletableFuture<?>> futures, EmbedBuilder builder) {
        Object nameObj = authorMap.get("name");
        if (nameObj != null) {
            CompletableFuture<String> nameFuture = MessageFormatterUtils.format(nameObj.toString(), event, context, false);
            CompletableFuture<String> urlFuture = getSafeUrl(authorMap.get("url"), event, context);
            CompletableFuture<String> iconUrlFuture = getSafeUrl(authorMap.get("icon_url"), event, context);

            futures.add(CompletableFuture.allOf(nameFuture, urlFuture, iconUrlFuture)
                    .thenRun(() -> builder.setAuthor(nameFuture.join(), urlFuture.join(), iconUrlFuture.join())));
        }
    }

    private static void processFooter(Map<String, Object> footerMap, Interaction event, Context context, List<CompletableFuture<?>> futures, EmbedBuilder builder) {
        Object textObj = footerMap.get("text");
        if (textObj != null) {
            CompletableFuture<String> textFuture = MessageFormatterUtils.format(textObj.toString(), event, context, false);
            CompletableFuture<String> iconUrlFuture = getSafeUrl(footerMap.get("icon_url"), event, context);

            futures.add(CompletableFuture.allOf(textFuture, iconUrlFuture)
                    .thenRun(() -> builder.setFooter(textFuture.join(), iconUrlFuture.join())));
        }
    }

    private static void processFields(List<Map<String, Object>> fields, Interaction event, Context context, List<CompletableFuture<?>> futures, EmbedBuilder builder) {
        for (Map<String, Object> field : fields) {
            Object nameObj = field.get("name");
            Object valueObj = field.get("value");
            if (nameObj != null && valueObj != null) {
                CompletableFuture<String> nameFuture = MessageFormatterUtils.format(nameObj.toString(), event, context, false);
                CompletableFuture<String> valueFuture = MessageFormatterUtils.format(valueObj.toString(), event, context, false);
                boolean inline = (Boolean) field.getOrDefault("inline", false);

                futures.add(CompletableFuture.allOf(nameFuture, valueFuture)
                        .thenRun(() -> builder.addField(nameFuture.join(), valueFuture.join(), inline)));
            }
        }
    }
    
    private static boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private static CompletableFuture<String> getSafeUrl(Object raw, Interaction event, Context context) {
        if (raw == null) return CompletableFuture.completedFuture(null);
        return MessageFormatterUtils.format(raw.toString(), event, context, false)
                .thenApply(formatted -> isValidUrl(formatted) ? formatted : null);
    }

    private static int parseColor(Object color) throws NumberFormatException {
        if (color instanceof Integer) return (Integer) color;
        String str = color.toString().trim();
        if (str.startsWith("#")) str = str.substring(1);
        return Integer.parseInt(str, 16);
    }
}