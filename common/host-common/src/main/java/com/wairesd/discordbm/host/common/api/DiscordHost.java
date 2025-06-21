package com.wairesd.discordbm.host.common.api;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.commandbuilder.commands.core.CommandManager;
import com.wairesd.discordbm.host.common.discord.DiscordBotManager;
import com.wairesd.discordbm.host.common.network.NettyServer;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Общий интерфейс для взаимодействия между host-common и конкретными реализациями Discord бота (например, DiscordBMV).
 * Это позволяет host-common не иметь прямых зависимостей от DiscordBMV.
 */
public interface DiscordHost {
    
    /**
     * Получить карту ожидающих запросов кнопок
     * 
     * @return Карта ожидающих запросов кнопок, где ключ - UUID запроса
     */
    Map<UUID, Object> getPendingButtonRequests();
    
    /**
     * Получить карту страниц для навигации
     * 
     * @return Карта страниц, где ключ - ID страницы, значение - объект Page
     */
    Map<String, Page> getPageMap();
    
    /**
     * Получить объект NettyServer
     * 
     * @return Экземпляр NettyServer
     */
    NettyServer getNettyServer();
    
    /**
     * Получить менеджер Discord бота
     * 
     * @return Экземпляр DiscordBotManager
     */
    DiscordBotManager getDiscordBotManager();
    
    /**
     * Получить менеджер команд
     * 
     * @return Экземпляр CommandManager
     */
    CommandManager getCommandManager();
    
    /**
     * Получить обработчики форм
     * 
     * @return Карта обработчиков форм
     */
    Map<String, Object> getFormHandlers();
    
    /**
     * Обновить активность бота
     */
    void updateActivity();
    
    /**
     * Получить экземпляр Velocity ProxyServer
     * 
     * @return Экземпляр ProxyServer
     */
    ProxyServer getVelocityProxy();
    
    /**
     * Установить глобальную метку сообщения
     * 
     * @param key Ключ метки
     * @param channelId ID канала
     * @param messageId ID сообщения
     */
    void setGlobalMessageLabel(String key, String channelId, String messageId);
    
    /**
     * Получить ID сообщения по глобальной метке
     * 
     * @param key Ключ метки
     * @return ID сообщения или null, если метка не найдена
     */
    String getGlobalMessageLabel(String key);
    
    /**
     * Получить ссылку на сообщение по глобальной метке
     * 
     * @param key Ключ метки
     * @return Массив из двух строк [channelId, messageId] или null, если метка не найдена
     */
    String[] getMessageReference(String key);
    
    /**
     * Получить все ссылки на сообщения с определенной меткой
     * 
     * @param labelPrefix Префикс метки (без ID гильдии)
     * @param guildId ID гильдии
     * @return Список пар [channelId, messageId] для всех сообщений с этой меткой
     * @deprecated Пока не используется, но сохранено для потенциального использования в будущем
     */
    @Deprecated
    List<String[]> getAllMessageReferences(String labelPrefix, String guildId);
    
    /**
     * Удалить глобальную метку сообщения
     * 
     * @param key Ключ метки
     */
    void removeGlobalMessageLabel(String key);
} 