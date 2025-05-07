package com.wairesd.discordbm.velocity.commands.commandbuilder.parser;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons.ButtonAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.components.EditComponentAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages.DeleteMessageAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages.SendMessageAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.ResolvePlaceholdersAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;

import java.util.Map;

public class CommandParserAction {

    public static CommandAction parseAction(Map<String, Object> actionMap, DiscordBMV plugin) {
        String type = (String) actionMap.get("type");
        if (type == null) {
            throw new IllegalArgumentException("Action type is required");
        }

        return switch (type.toLowerCase()) {
            case "send_message" -> new SendMessageAction(actionMap);
            case "button" -> new ButtonAction(actionMap);
            case "edit_component" -> new EditComponentAction(actionMap);
            case "resolve_placeholders" -> new ResolvePlaceholdersAction(actionMap, plugin);
            case "delete_message" -> new DeleteMessageAction(actionMap);
            default -> throw new IllegalArgumentException("Unknown action type: " + type);
        };
    }
}