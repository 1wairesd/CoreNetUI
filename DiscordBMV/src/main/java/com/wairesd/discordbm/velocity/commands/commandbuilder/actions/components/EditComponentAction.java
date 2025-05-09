package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.components;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EditComponentAction implements CommandAction {
    private final String targetMessageLabel;
    private final String componentId;
    private final String newLabel;
    private final String newStyle;
    private final Boolean disabled;

    public EditComponentAction(Map<String, Object> props) {
        this.targetMessageLabel = (String) props.get("target_message");
        this.componentId = (String) props.get("component_id");
        this.newLabel = (String) props.get("label");
        this.newStyle = (String) props.get("style");
        this.disabled = (Boolean) props.get("disabled");

        if (componentId == null || componentId.isEmpty()) {
            throw new IllegalArgumentException("component_id is required for EditComponentAction");
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        return CompletableFuture.runAsync(() -> {
            String messageId = targetMessageLabel != null ? context.getMessageIdByLabel(targetMessageLabel) : context.getMessageIdToEdit();
            if (context.getEvent().getChannel() instanceof TextChannel textChannel) {
                textChannel.retrieveMessageById(messageId).queue(
                        message -> context.setActionRows(message.getActionRows()),
                        throwable -> {}
                );
            }

            List<ActionRow> actionRows = context.getActionRows();
            for (int i = 0; i < actionRows.size(); i++) {
                ActionRow row = actionRows.get(i);
                List<ItemComponent> components = row.getComponents();
                for (int j = 0; j < components.size(); j++) {
                    ItemComponent component = components.get(j);
                    if (component instanceof Button button && button.getId() != null && button.getId().equals(componentId)) {
                        ButtonStyle style = newStyle != null ? ButtonStyle.valueOf(newStyle.toUpperCase()) : button.getStyle();
                        String label = newLabel != null ? newLabel : button.getLabel();
                        boolean isDisabled = disabled != null ? disabled : button.isDisabled();
                        Button newButton = button.withStyle(style).withLabel(label).withDisabled(isDisabled);
                        List<ItemComponent> newComponents = new ArrayList<>(components);
                        newComponents.set(j, newButton);
                        ActionRow newRow = ActionRow.of(newComponents);
                        actionRows.set(i, newRow);
                        return;
                    }
                }
            }
        });
    }
}