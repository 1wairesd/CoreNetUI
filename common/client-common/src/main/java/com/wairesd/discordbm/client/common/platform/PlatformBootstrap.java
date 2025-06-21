package com.wairesd.discordbm.client.common.platform;

import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.client.common.config.ConfigManager;

/**
 * Интерфейс для бутстрапа платформы
 */
public interface PlatformBootstrap {
    
    /**
     * Инициализирует платформу
     */
    void initialize();
    
    /**
     * Получает платформу
     * @return Платформа
     */
    Platform getPlatform();
    
    /**
     * Получает API
     * @return API
     */
    DiscordBMAPI getApi();
    
    /**
     * Получает менеджер конфигурации
     * @return Менеджер конфигурации
     */
    ConfigManager getConfigManager();
} 