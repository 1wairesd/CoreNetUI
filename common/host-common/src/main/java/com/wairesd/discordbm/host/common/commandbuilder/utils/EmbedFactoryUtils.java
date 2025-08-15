package com.wairesd.discordbm.host.common.commandbuilder.utils;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
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

    private static final PluginLogger logger =
            new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));

    public static CompletableFuture<MessageEmbed> create(
            Map<String, Object> embedMap, Interaction event, Context context) {
        EmbedBuilder builder = new EmbedBuilder();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        processBasicStrings(embedMap, event, context, futures, builder);
        processUrls(embedMap, event, context, futures, builder);
        processColor(embedMap, builder);
        processAuthor(embedMap, event, context, futures, builder);
        processFooter(embedMap, event, context, futures, builder);
        processFields(embedMap, event, context, futures, builder);

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> builder.build());
    }

    private static void processBasicStrings(
            Map<String, Object> embedMap,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
        processString(embedMap, "title", event, context, futures, builder::setTitle);
        processString(embedMap, "description", event, context, futures, builder::setDescription);
    }

    private static void processUrls(
            Map<String, Object> embedMap,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
        processUrl(embedMap, "thumbnail", event, context, futures, builder::setThumbnail);
        processUrl(embedMap, "image", event, context, futures, builder::setImage);
    }

    private static void processColor(Map<String, Object> embedMap, EmbedBuilder builder) {
        if (embedMap.containsKey("color")) {
            try {
                builder.setColor(parseColor(embedMap.get("color")));
            } catch (NumberFormatException e) {
                logger.warn("Invalid color format: {}", embedMap.get("color"));
            }
        }
    }

    private static void processAuthor(
            Map<String, Object> embedMap,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
        Object authorObj = embedMap.get("author");
        if (authorObj instanceof Map) {
            processAuthorMap((Map<String, Object>) authorObj, event, context, futures, builder);
        } else if (authorObj instanceof String) {
            processString(embedMap, "author", event, context, futures,
                    name -> builder.setAuthor(name, null, null));
        }
    }

    private static void processFooter(
            Map<String, Object> embedMap,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
        Object footerObj = embedMap.get("footer");
        if (footerObj instanceof Map) {
            processFooterMap((Map<String, Object>) footerObj, event, context, futures, builder);
        } else if (footerObj instanceof String) {
            processString(embedMap, "footer", event, context, futures, text -> builder.setFooter(text, null));
        }
    }

    private static void processFields(
            Map<String, Object> embedMap,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
        Object fieldsObj = embedMap.get("fields");
        if (fieldsObj instanceof List) {
            processFieldsList((List<Map<String, Object>>) fieldsObj, event, context, futures, builder);
        }
    }

    private static void processString(
            Map<String, Object> map,
            String key,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            Consumer<String> consumer) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value != null) {
                futures.add(MessageFormatterUtils.format(value.toString(), event, context, false)
                        .thenAccept(consumer));
            }
        }
    }

    private static void processUrl(
            Map<String, Object> map,
            String key,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            Consumer<String> consumer) {
        processString(map, key, event, context, futures, url -> {
            if (isValidUrl(url)) {
                consumer.accept(url);
            }
        });
    }

    private static void processAuthorMap(
            Map<String, Object> authorMap,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
        Object nameObj = authorMap.get("name");
        if (nameObj != null) {
            CompletableFuture<String> nameFuture = MessageFormatterUtils.format(nameObj.toString(), event, context, false);
            CompletableFuture<String> urlFuture = getSafeUrl(authorMap.get("url"), event, context);
            CompletableFuture<String> iconUrlFuture = getSafeUrl(authorMap.get("icon_url"), event, context);

            futures.add(CompletableFuture.allOf(nameFuture, urlFuture, iconUrlFuture)
                    .thenRun(() -> builder.setAuthor(nameFuture.join(), urlFuture.join(), iconUrlFuture.join())));
        }
    }

    private static void processFooterMap(
            Map<String, Object> footerMap,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
        Object textObj = footerMap.get("text");
        if (textObj != null) {
            CompletableFuture<String> textFuture = MessageFormatterUtils.format(textObj.toString(), event, context, false);
            CompletableFuture<String> iconUrlFuture = getSafeUrl(footerMap.get("icon_url"), event, context);

            futures.add(CompletableFuture.allOf(textFuture, iconUrlFuture)
                    .thenRun(() -> builder.setFooter(textFuture.join(), iconUrlFuture.join())));
        }
    }

    private static void processFieldsList(
            List<Map<String, Object>> fields,
            Interaction event,
            Context context,
            List<CompletableFuture<?>> futures,
            EmbedBuilder builder) {
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
