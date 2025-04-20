package com.wairesd.discordbm.velocity.command.custom.actions;

import com.wairesd.discordbm.velocity.command.custom.models.CommandAction;
import com.wairesd.discordbm.velocity.command.custom.models.Context;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Map;
import java.util.UUID;


public class ButtonAction implements CommandAction {
    private final String label;
    private final String style;
    private final String url;
    private final String message;
    private final String emoji;
    private final boolean disabled;

    public ButtonAction(Map<String, Object> props) {
        this.label = (String) props.getOrDefault("label", "Button");
        this.style = ((String) props.getOrDefault("style", "PRIMARY")).toUpperCase();
        this.url = (String) props.getOrDefault("url", "");
        this.message = (String) props.getOrDefault("message", "");
        this.emoji = (String) props.getOrDefault("emoji", "");
        this.disabled = (boolean) props.getOrDefault("disabled", false);

        // Валидация параметров
        if (label.isEmpty()) throw new IllegalArgumentException("label is required");
        if (style.equalsIgnoreCase("LINK") && url.isEmpty()) {
            throw new IllegalArgumentException("url property is required for LINK button");
        }
        if (!style.equalsIgnoreCase("LINK") && message.isEmpty()) {
            throw new IllegalArgumentException("message property is required for non-LINK button");
        }
    }

    @Override
    public void execute(Context context) {
        String customId = "btn-" + UUID.randomUUID();
        Button button;

        ButtonStyle btnStyle = ButtonStyle.valueOf(style);

        if (style.equalsIgnoreCase("LINK")) {
            button = Button.link(url, label);
        } else {
            ButtonActionRegistry.register(customId, message, 5 * 60 * 1000);
            button = Button.of(btnStyle, customId, label);
        }

        if (!emoji.isEmpty()) {
            button = button.withEmoji(Emoji.fromUnicode(emoji));
        }

        if (disabled) {
            button = button.asDisabled();
        }

        context.addButton(button);
    }
}