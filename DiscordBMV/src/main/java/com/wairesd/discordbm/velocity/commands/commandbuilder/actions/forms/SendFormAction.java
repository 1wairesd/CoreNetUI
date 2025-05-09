package com.wairesd.discordbm.velocity.commands.commandbuilder.actions.forms;

import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.actions.CommandAction;
import com.wairesd.discordbm.velocity.commands.commandbuilder.models.contexts.Context;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SendFormAction implements CommandAction {
    private final String formName;
    private final String title;
    private final List<Map<String, Object>> fields;

    public SendFormAction(Map<String, Object> properties) {
        this.formName = (String) properties.get("form_name");
        this.title = (String) properties.get("title");
        this.fields = (List<Map<String, Object>>) properties.get("fields");
        validateProperties();
    }

    private void validateProperties() {
        if (formName == null || title == null || fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("Form name, title, and fields are required");
        }
        for (Map<String, Object> field : fields) {
            if (!field.containsKey("label") || !field.containsKey("variable")) {
                throw new IllegalArgumentException("Each field must have a label and variable name");
            }
        }
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        String uniqueID = UUID.randomUUID().toString();
        String modalID = "form_" + uniqueID;
        CompletableFuture<Void> completionFuture = new CompletableFuture<>();
        DiscordBMV.plugin.getFormHandlers().put(modalID, new ImmutablePair<>(completionFuture, context));

        Modal.Builder modalBuilder = Modal.create(modalID, title);
        for (Map<String, Object> field : fields) {
            String label = (String) field.get("label");
            String placeholder = (String) field.getOrDefault("placeholder", "");
            int minLength = field.containsKey("min_length") ? ((Number) field.get("min_length")).intValue() : 0;
            int maxLength = field.containsKey("max_length") ? ((Number) field.get("max_length")).intValue() : 4000;
            String type = (String) field.getOrDefault("type", "SHORT");
            boolean required = (boolean) field.getOrDefault("required", false);
            String defaultValue = (String) field.getOrDefault("default", "");
            String variableName = (String) field.get("variable");

            TextInput.Builder inputBuilder = TextInput.create(variableName, label, TextInputStyle.valueOf(type.toUpperCase()))
                    .setPlaceholder(placeholder)
                    .setMinLength(minLength)
                    .setMaxLength(maxLength)
                    .setRequired(required);

            if (!defaultValue.isBlank()) {
                inputBuilder.setValue(defaultValue);
            }

            modalBuilder.addActionRow(inputBuilder.build());
        }

        Modal modal = modalBuilder.build();

        if (context.getEvent() instanceof SlashCommandInteractionEvent event) {
            try {
                event.replyModal(modal).queue(
                        success -> {},
                        error -> {
                            context.getHook().sendMessage("The form could not be opened. Try again.")
                                    .setEphemeral(true)
                                    .queue();
                            completionFuture.completeExceptionally(error);
                        }
                );
            } catch (IllegalStateException e) {
                context.getHook().sendMessage("The form could not be opened. Try again.")
                        .setEphemeral(true)
                        .queue();
                completionFuture.completeExceptionally(e);
            }
        } else {
            completionFuture.completeExceptionally(
                    new IllegalStateException("This interaction does not support modal replies.")
            );
        }

        return completionFuture;
    }
}