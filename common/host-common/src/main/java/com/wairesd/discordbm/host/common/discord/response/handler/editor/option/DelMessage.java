package com.wairesd.discordbm.host.common.discord.response.handler.editor.option;

import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.messages.DeleteMessageAction;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DelMessage {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));

    public static void deleteMessage(ResponseMessage respMsg) {
        String label = respMsg.requestId();
        if (label == null) {
            logger.error("No label provided for delete_message");
            return;
        }
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("label", label);
            properties.put("delete_all", respMsg.deleteAll());
            DeleteMessageAction action = new DeleteMessageAction(properties);
            action.execute(null).join();
        } catch (Exception e) {
            logger.error("Failed to delete message for label: {}", label, e);
        }
    }
}
