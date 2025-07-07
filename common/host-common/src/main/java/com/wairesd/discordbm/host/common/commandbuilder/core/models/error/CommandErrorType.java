package com.wairesd.discordbm.host.common.commandbuilder.core.models.error;

public enum CommandErrorType {
    PERMISSION_DENIED,          // Нет необходимых прав
    ROLE_REQUIRED,             // Нет необходимой роли
    COOLDOWN_ACTIVE,           // Команда на перезарядке
    INVALID_CONTEXT,           // Неверный контекст (DM/сервер)
    INVALID_ARGUMENTS,         // Неверные аргументы
    SERVER_ERROR,              // Ошибка сервера
    PLAYER_NOT_FOUND,          // Игрок не найден
    PLAYER_OFFLINE,            // Игрок не в сети
    INSUFFICIENT_BALANCE,      // Недостаточно средств/ресурсов
    TARGET_NOT_FOUND,          // Целевой объект не найден
    INVALID_PERMISSION,        // Некорректные права
    ALREADY_EXISTS,            // Объект уже существует
    DOES_NOT_EXIST,           // Объект не существует
    INVALID_FORMAT,           // Неверный формат данных
    OPERATION_FAILED,          // Операция не удалась
    CHANCE_FAILED,             // Не выполнено условие chance
    DM_FAILED                  // Не удалось отправить ЛС
} 