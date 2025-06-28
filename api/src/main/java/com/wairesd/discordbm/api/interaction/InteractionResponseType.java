package com.wairesd.discordbm.api.interaction;

public enum InteractionResponseType {
    AUTO,        // Сервер сам решает по аргументам
    REPLY_MODAL, // replyModal()
    DEFER_REPLY, // deferReply() + send
    REPLY        // reply() сразу
} 