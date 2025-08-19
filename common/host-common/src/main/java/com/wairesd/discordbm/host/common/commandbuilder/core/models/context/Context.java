package com.wairesd.discordbm.host.common.commandbuilder.core.models.context;

import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.model.ButtonData;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
    private Interaction event;
    private String messageText = "";
    private ResponseType responseType = ResponseType.REPLY;
    private String targetChannelId;
    private String targetUserId;
    private String messageIdToEdit;
    private String resolvedMessage;
    private MessageEmbed embed;
    private final List<ActionRow> actionRows = new ArrayList<>();
    private final Map<String, String> messageLabels = new ConcurrentHashMap<>();
    private String expectedMessageLabel;
    private Map<String, String> variables = new HashMap<>();
    private InteractionHook hook;
    private Map<String, String> formResponses;
    private User targetUser;
    private Map<String, String> resolvedPlaceholders = new HashMap<>();
    private ButtonData buttonData;
    private List<String> messageList;
    private boolean ephemeral = false;

    public Context(ModalInteractionEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        this.event = event;
    }

    public Context(ButtonInteractionEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        this.event = event;
    }

    public Context(SlashCommandInteractionEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        this.event = event;
    }

    public String replacePlaceholders(String text) {
        if (text == null) return text;
        
        String result = text;

        if (formResponses != null) {
            for (Map.Entry<String, String> entry : formResponses.entrySet()) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        if (event instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent slashEvent = (SlashCommandInteractionEvent) event;

            int optionStartIdx = result.indexOf("{option:");
            while (optionStartIdx != -1) {
                int optionEndIdx = result.indexOf("}", optionStartIdx);
                if (optionEndIdx != -1) {
                    String placeholder = result.substring(optionStartIdx, optionEndIdx + 1);
                    String content = result.substring(optionStartIdx + 8, optionEndIdx);
                    
                    String optionName;
                    String defaultValue = "";

                    int pipeIdx = content.indexOf('|');
                    if (pipeIdx != -1) {
                        optionName = content.substring(0, pipeIdx);
                        defaultValue = content.substring(pipeIdx + 1);
                    } else {
                        optionName = content;
                    }

                    OptionMapping option = slashEvent.getOption(optionName);
                    String replacement = defaultValue;
                    
                    if (option != null) {
                        try {
                            switch (option.getType()) {
                                case STRING, INTEGER, NUMBER, BOOLEAN -> replacement = option.getAsString();
                                case USER -> replacement = option.getAsUser().getAsMention();
                                case CHANNEL -> replacement = option.getAsChannel().getAsMention();
                                case ROLE -> replacement = option.getAsRole().getAsMention();
                                case MENTIONABLE -> {
                                    if (option.getAsMentionable() != null) {
                                        replacement = option.getAsMentionable().getAsMention();
                                    }
                                }
                                default -> replacement = option.getAsString();
                            }
                        } catch (Exception e) {
                        }
                    }
                    
                    result = result.replace(placeholder, replacement);
                }
                
                optionStartIdx = result.indexOf("{option:", optionStartIdx + 1);
            }
        }

        if (resolvedPlaceholders != null) {
            for (Map.Entry<String, String> entry : resolvedPlaceholders.entrySet()) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        
        return result;
    }

    public String getOption(String name) {
        if (event instanceof SlashCommandInteractionEvent) {
            SlashCommandInteractionEvent slashEvent = (SlashCommandInteractionEvent) event;
            OptionMapping option = slashEvent.getOption(name);
            return option != null ? option.getAsString() : null;
        }
        return null;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public void setFormResponses(Map<String, String> responses) {
        this.formResponses = responses;
    }

    public Interaction getEvent() {
        return event;
    }

    public InteractionHook getHook() {
        return hook;
    }

    public void setHook(InteractionHook hook) {
        this.hook = hook;
    }

    public void setEvent(Interaction event) {
        this.event = event;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public String getTargetChannelId() {
        return targetChannelId;
    }

    public void setTargetChannelId(String targetChannelId) {
        this.targetChannelId = targetChannelId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getMessageIdToEdit() {
        return messageIdToEdit;
    }

    public void setMessageIdToEdit(String messageIdToEdit) {
        this.messageIdToEdit = messageIdToEdit;
    }

    public String getResolvedMessage() {
        return resolvedMessage;
    }

    public void setResolvedMessage(String resolvedMessage) {
        this.resolvedMessage = resolvedMessage;
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    public void setEmbed(MessageEmbed embed) {
        this.embed = embed;
    }

    public List<ActionRow> getActionRows() {
        return actionRows;
    }

    public void setActionRows(List<ActionRow> actionRows) {
        this.actionRows.clear();
        this.actionRows.addAll(actionRows);
    }

    public void addActionRow(ActionRow actionRow) {
        this.actionRows.add(actionRow);
    }

    public void setExpectedMessageLabel(String label) {
        this.expectedMessageLabel = label;
    }

    public String getExpectedMessageLabel() {
        return expectedMessageLabel;
    }

    public String getMessageIdByLabel(String label) {
        return messageLabels.get(label);
    }

    public Map<String, String> getResolvedPlaceholders() {
        return resolvedPlaceholders;
    }

    public void setResolvedPlaceholders(Map<String, String> resolvedPlaceholders) {
        this.resolvedPlaceholders = resolvedPlaceholders;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public List<String> getMessageList() {
        return messageList;
    }
    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }
}
