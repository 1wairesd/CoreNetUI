package com.wairesd.discordbm.host.common.discord.response.handler.editor;

import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.discord.response.handler.editor.option.Component;
import com.wairesd.discordbm.host.common.discord.response.handler.editor.option.DelMessage;
import com.wairesd.discordbm.host.common.discord.response.handler.editor.option.EditMessage;
import com.wairesd.discordbm.host.common.utils.Components;
import org.slf4j.LoggerFactory;

public class MessageEditor {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static DiscordBMHPlatformManager platformManager;
    private static Components components;


    public static void deleteMessage(ResponseMessage respMsg) {
        DelMessage.deleteMessage(respMsg);
    }

    public static void editComponent(ResponseMessage respMsg) {
        Component.editComponent(respMsg);
    }

    public static void editMessage(ResponseMessage respMsg) {
        EditMessage.editMessage(respMsg);
    }
}
