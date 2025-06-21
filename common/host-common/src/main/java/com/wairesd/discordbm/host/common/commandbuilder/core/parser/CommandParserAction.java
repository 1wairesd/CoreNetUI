package com.wairesd.discordbm.host.common.commandbuilder.core.parser;

import com.wairesd.discordbm.host.common.api.DiscordHost;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.action.ButtonAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.components.EditComponentAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.messages.DeleteMessageAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.messages.SendMessageAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.messages.SendToChannelAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.page.SendPageAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.placeholders.ResolvePlaceholdersAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.roles.AddRoleAction;
import com.wairesd.discordbm.host.common.commandbuilder.interaction.roles.RemoveRoleAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.components.forms.action.SendFormAction;

import java.util.Map;

public class CommandParserAction {
    public static CommandAction parseAction(Map<String, Object> actionMap, DiscordHost discordHost) {
        String type = (String) actionMap.get("type");
        if (type == null) {
            throw new IllegalArgumentException("Action type is required");
        }
        return switch (type.toLowerCase()) {
            case "send_message" -> new SendMessageAction(actionMap);
            case "send_to_channel" -> new SendToChannelAction(actionMap);
            case "button" -> new ButtonAction(actionMap);
            case "edit_component" -> new EditComponentAction(actionMap);
            case "resolve_placeholders" -> new ResolvePlaceholdersAction(actionMap, discordHost);
            case "delete_message" -> new DeleteMessageAction(actionMap);
            case "send_form" -> new SendFormAction(actionMap, discordHost);
            case "add_role" -> new AddRoleAction(actionMap);
            case "remove_role" -> new RemoveRoleAction(actionMap);
            case "send_page" -> new SendPageAction(actionMap);
            default -> throw new IllegalArgumentException("Unknown action type: " + type);
        };
    }
}