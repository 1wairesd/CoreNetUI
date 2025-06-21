package com.wairesd.discordbm.client.common.platform;

import java.io.File;
import java.io.InputStream;

/**
 * Интерфейс для работы с конфигурациями плагина на разных платформах (Bukkit, Velocity и т.д.)
 */
public interface PlatformConfig {

    /**
     * Получить директорию данных плагина
     *
     * @return Директория данных плагина
     */
    File getDataFolder();

    /**
     * Получить ресурс из jar-файла плагина
     *
     * @param resourceName Имя ресурса
     * @return InputStream ресурса или null, если ресурс не найден
     */
    InputStream getResource(String resourceName);

    /**
     * Сохранить ресурс из jar-файла плагина в директорию данных
     *
     * @param resourceName Имя ресурса
     * @param replace      Заменять ли существующий файл
     */
    void saveResource(String resourceName, boolean replace);

    /**
     * Получить имя плагина
     *
     * @return Имя плагина
     */
    String getPluginName();

    /**
     * Получить версию плагина
     *
     * @return Версия плагина
     */
    String getPluginVersion();
    
    /**
     * Логирование информационного сообщения
     * 
     * @param message Сообщение
     */
    void logInfo(String message);
    
    /**
     * Логирование сообщения об ошибке
     * 
     * @param message Сообщение
     * @param throwable Исключение (может быть null)
     */
    void logError(String message, Throwable throwable);
    
    /**
     * Логирование предупреждения
     * 
     * @param message Сообщение
     */
    void logWarning(String message);
} 