package com.wairesd.discordbm.host.common.commandbuilder.core.models.error;

import com.wairesd.discordbm.host.common.config.configurators.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CommandErrorMessages {
    private static final Map<CommandErrorType, String> DEFAULT_MESSAGES = new HashMap<>();
    private static final Map<CommandErrorType, String> CUSTOM_MESSAGES = new HashMap<>();
    private static final Color ERROR_COLOR = new Color(255, 0, 0);

    static {
        DEFAULT_MESSAGES.put(CommandErrorType.PERMISSION_DENIED, "❌ У вас нет прав для выполнения этой команды");
        DEFAULT_MESSAGES.put(CommandErrorType.ROLE_REQUIRED, Messages.getMessage(Messages.Keys.ROLE_REQUIRED));
        DEFAULT_MESSAGES.put(CommandErrorType.COOLDOWN_ACTIVE, "⏳ Подождите {0} секунд перед повторным использованием");
        DEFAULT_MESSAGES.put(CommandErrorType.INVALID_CONTEXT, "❌ Эта команда не может быть использована здесь");
        DEFAULT_MESSAGES.put(CommandErrorType.INVALID_ARGUMENTS, "❌ Неверные аргументы команды: {0}");
        DEFAULT_MESSAGES.put(CommandErrorType.SERVER_ERROR, "⚠️ Произошла ошибка сервера. Попробуйте позже");
        DEFAULT_MESSAGES.put(CommandErrorType.PLAYER_NOT_FOUND, "❌ Игрок {0} не найден");
        DEFAULT_MESSAGES.put(CommandErrorType.PLAYER_OFFLINE, "❌ Игрок {0} не в сети");
        DEFAULT_MESSAGES.put(CommandErrorType.INSUFFICIENT_BALANCE, "❌ Недостаточно {0}");
        DEFAULT_MESSAGES.put(CommandErrorType.TARGET_NOT_FOUND, "❌ {0} не найден");
        DEFAULT_MESSAGES.put(CommandErrorType.INVALID_PERMISSION, "❌ Некорректные права: {0}");
        DEFAULT_MESSAGES.put(CommandErrorType.ALREADY_EXISTS, "❌ {0} уже существует");
        DEFAULT_MESSAGES.put(CommandErrorType.DOES_NOT_EXIST, "❌ {0} не существует");
        DEFAULT_MESSAGES.put(CommandErrorType.INVALID_FORMAT, "❌ Неверный формат: {0}");
        DEFAULT_MESSAGES.put(CommandErrorType.OPERATION_FAILED, "❌ Не удалось выполнить операцию: {0}");
        DEFAULT_MESSAGES.put(CommandErrorType.CHANCE_FAILED, Messages.getMessage(Messages.Keys.CHANCE_FAILED));
    }

    public static void setCustomMessage(CommandErrorType type, String message) {
        CUSTOM_MESSAGES.put(type, message);
    }

    public static String getMessage(CommandErrorType type) {
        return CUSTOM_MESSAGES.getOrDefault(type, DEFAULT_MESSAGES.get(type));
    }

    public static String formatMessage(CommandErrorType type, Map<String, String> placeholders) {
        String message = getMessage(type);
        if (message == null) return "Неизвестная ошибка";

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public static MessageEmbed createErrorEmbed(CommandErrorType type, Map<String, String> placeholders) {
        String message = formatMessage(type, placeholders);
        return new EmbedBuilder()
                .setDescription(message)
                .setColor(ERROR_COLOR)
                .build();
    }

    public static MessageEmbed createErrorEmbed(CommandErrorType type) {
        return createErrorEmbed(type, new HashMap<>());
    }
} 