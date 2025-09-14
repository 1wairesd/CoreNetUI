package com.wairesd.discordbm.api.interaction;

public enum InteractionResponseType {
    AUTO,        // The server decides automatically based on arguments
    REPLY_MODAL, // replyModal()
    DEFER_REPLY, // deferReply() + send
    REPLY        // reply() immediately
}