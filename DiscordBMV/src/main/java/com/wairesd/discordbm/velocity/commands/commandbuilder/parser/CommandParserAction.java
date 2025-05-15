package com.wairesd.discordbm.velocity.commands.commandbuilder.parser;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons.ButtonAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.components.EditComponentAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages.DeleteMessageAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.forms.SendFormAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.messages.SendMessageAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.placeholders.ResolvePlaceholdersAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.roles.AddRoleAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.actions.roles.RemoveRoleAction;
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
            case "send_form" -> new SendFormAction(actionMap);
            case "add_role" -> new AddRoleAction(actionMap);
            case "remove_role" -> new RemoveRoleAction(actionMap);
            default -> throw new IllegalArgumentException("Unknown action type: " + type);
        };
    }
}