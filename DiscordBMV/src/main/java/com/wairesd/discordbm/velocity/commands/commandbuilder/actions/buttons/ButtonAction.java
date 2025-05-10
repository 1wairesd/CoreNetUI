package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.buttons;

import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import com.wairesd.discordbm.velocity.config.configurators.Settings;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
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
    private final String customId;
    private final String formName;
    private final String requiredRoleId;
    private final long timeoutMs;

    public ButtonAction(Map<String, Object> props) {
        validateProps(props);

        this.label = (String) props.getOrDefault("label", DEFAULT_LABEL);
        this.style = ButtonStyle.valueOf(((String) props.getOrDefault("style", DEFAULT_STYLE)).toUpperCase());
        this.url = (String) props.getOrDefault("url", DEFAULT_URL);
        this.message = (String) props.getOrDefault("message", DEFAULT_MESSAGE);
        this.emoji = (String) props.getOrDefault("emoji", DEFAULT_EMOJI);
        this.disabled = (boolean) props.getOrDefault("disabled", DEFAULT_DISABLED);
        this.customId = (String) props.get("id");
        this.formName = (String) props.get("form_name");
        this.requiredRoleId = (String) props.get("required_role");
        this.timeoutMs = parseTimeout(props.get("timeout"));
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
        } else if (!props.containsKey("form_name") && ((String) props.getOrDefault("message", "")).isEmpty()) {
            throw new IllegalArgumentException("message or form_name is required for non-LINK button");
        }
    }

    private long parseTimeout(Object timeoutObj) {
        if (timeoutObj == null) {
            return Settings.getButtonTimeoutMs();
        }

        if (timeoutObj instanceof String str) {
            if (str.equalsIgnoreCase("infinite")) {
                return Long.MAX_VALUE;
            } else {
                try {
                    return Long.parseLong(str) * 60_000;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid timeout format: " + str);
                }
            }
        } else if (timeoutObj instanceof Number number) {
            return number.longValue() * 60_000;
        }

        throw new IllegalArgumentException("Unsupported timeout value: " + timeoutObj);
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        return CompletableFuture.runAsync(() -> {
            String buttonId = customId != null ? customId : generateCustomId();
            Button button = createButton(buttonId);
            button = applyEmojiAndDisabledState(button);
            context.addActionRow(ActionRow.of(button));
        });
    }

    private String generateCustomId() {
        return "btn-" + UUID.randomUUID();
    }

    private Button createButton(String buttonId) {
        if (style == ButtonStyle.LINK) {
            return Button.link(url, label);
        } else if (formName != null) {
            ButtonActionRegistry.registerFormButton(buttonId, formName, message, requiredRoleId, timeoutMs);
            return Button.of(style, buttonId, label);
        } else {
            ButtonActionRegistry.register(buttonId, message, timeoutMs);
            return Button.of(style, buttonId, label);
        }
    }

    private Button applyEmojiAndDisabledState(Button button) {
        if (!emoji.isEmpty()) {
            button = button.withEmoji(Emoji.fromUnicode(emoji));
        }
        if (disabled) {
            button = button.asDisabled();
        }
        return button;
    }
}