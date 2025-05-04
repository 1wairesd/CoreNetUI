package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ButtonAction implements CommandAction {
    private static final String DEFAULT_LABEL = "Button";
    private static final String DEFAULT_STYLE = "PRIMARY";
    private static final String DEFAULT_URL = "";
    private static final String DEFAULT_MESSAGE = "";
    private static final String DEFAULT_EMOJI = "";
    private static final boolean DEFAULT_DISABLED = false;

    private final String label;
    private final ButtonStyle style;
    private final String url;
    private final String message;
    private final String emoji;
    private final boolean disabled;

    public ButtonAction(Map<String, Object> props) {
        validateProps(props);

        this.label = (String) props.getOrDefault("label", DEFAULT_LABEL);
        this.style = ButtonStyle.valueOf(((String) props.getOrDefault("style", DEFAULT_STYLE)).toUpperCase());
        this.url = (String) props.getOrDefault("url", DEFAULT_URL);
        this.message = (String) props.getOrDefault("message", DEFAULT_MESSAGE);
        this.emoji = (String) props.getOrDefault("emoji", DEFAULT_EMOJI);
        this.disabled = (boolean) props.getOrDefault("disabled", DEFAULT_DISABLED);
    }

    private void validateProps(Map<String, Object> props) {
        String label = (String) props.get("label");
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("label is required");
        }

        String style = (String) props.getOrDefault("style", DEFAULT_STYLE);
        ButtonStyle buttonStyle = ButtonStyle.valueOf(style.toUpperCase());

        if (buttonStyle == ButtonStyle.LINK) {
            String url = (String) props.get("url");
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("url property is required for LINK button");
            }
        } else {
            String message = (String) props.get("message");
            if (message == null || message.isEmpty()) {
                throw new IllegalArgumentException("message property is required for non-LINK button");
            }
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        return CompletableFuture.runAsync(() -> {
            String customId = generateCustomId();
            Button button = createButton(customId);
            applyEmojiAndDisabledState(button);
            context.addButton(button);
        });
    }

    private String generateCustomId() {
        return "btn-" + UUID.randomUUID();
    }

    private Button createButton(String customId) {
        if (style == ButtonStyle.LINK) {
            return Button.link(url, label);
        } else {
            ButtonActionRegistry.register(customId, message, 60 * 60 * 1000);
            return Button.of(style, customId, label);
        }
    }

    private void applyEmojiAndDisabledState(Button button) {
        if (!emoji.isEmpty()) {
            button = button.withEmoji(Emoji.fromUnicode(emoji));
        }
        if (disabled) {
            button = button.asDisabled();
        }
    }
}
