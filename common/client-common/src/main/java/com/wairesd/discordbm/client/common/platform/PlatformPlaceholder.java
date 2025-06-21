package com.wairesd.discordbm.client.common.platform;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для платформенно-независимой службы подстановки плейсхолдеров
 */
public interface PlatformPlaceholder {
    
    /**
     * Проверить, может ли сервис обработать указанные плейсхолдеры для указанного игрока
     * 
     * @param playerName Имя или UUID игрока
     * @param placeholders Список плейсхолдеров для проверки
     * @return true, если сервис может обработать хотя бы один плейсхолдер
     */
    boolean checkIfCanHandle(String playerName, List<String> placeholders);
    
    /**
     * Получить значения плейсхолдеров для указанного игрока
     * 
     * @param playerName Имя или UUID игрока
     * @param placeholders Список плейсхолдеров
     * @return Карта, где ключ - плейсхолдер, значение - подставленное значение
     */
    Map<String, String> getPlaceholderValues(String playerName, List<String> placeholders);
} 