package com.wairesd.discordbm.host.common.commandbuilder.core.models.error;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandErrorHandler {
    private final Context context;
    private final SlashCommandInteractionEvent event;

    public CommandErrorHandler(Context context, SlashCommandInteractionEvent event) {
        this.context = context;
        this.event = event;
    }

    public void handlePermissionDenied(String permission) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("permission", permission);
        sendError(CommandErrorType.PERMISSION_DENIED, placeholders);
    }

    public void handleRoleRequired(String role) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("role", role);
        sendError(CommandErrorType.ROLE_REQUIRED, placeholders);
    }

    public void handleCooldown(long remainingSeconds) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("time", String.valueOf(remainingSeconds));
        sendError(CommandErrorType.COOLDOWN_ACTIVE, placeholders);
    }

    public void handleInvalidContext() {
        sendError(CommandErrorType.INVALID_CONTEXT);
    }

    public void handleInvalidArguments(String reason) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("reason", reason);
        sendError(CommandErrorType.INVALID_ARGUMENTS, placeholders);
    }

    public void handleServerError() {
        sendError(CommandErrorType.SERVER_ERROR);
    }

    public void handlePlayerNotFound(String player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player);
        sendError(CommandErrorType.PLAYER_NOT_FOUND, placeholders);
    }

    public void handlePlayerOffline(String player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player);
        sendError(CommandErrorType.PLAYER_OFFLINE, placeholders);
    }

    public void handleInsufficientBalance(String resource) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("resource", resource);
        sendError(CommandErrorType.INSUFFICIENT_BALANCE, placeholders);
    }

    public void handleTargetNotFound(String target) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("target", target);
        sendError(CommandErrorType.TARGET_NOT_FOUND, placeholders);
    }

    public void handleAlreadyExists(String object) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("object", object);
        sendError(CommandErrorType.ALREADY_EXISTS, placeholders);
    }

    public void handleDoesNotExist(String object) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("object", object);
        sendError(CommandErrorType.DOES_NOT_EXIST, placeholders);
    }

    public void handleInvalidFormat(String format) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("format", format);
        sendError(CommandErrorType.INVALID_FORMAT, placeholders);
    }

    public void handleOperationFailed(String reason) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("reason", reason);
        sendError(CommandErrorType.OPERATION_FAILED, placeholders);
    }

    private void sendError(CommandErrorType type, Map<String, String> placeholders) {
        MessageEmbed embed = CommandErrorMessages.createErrorEmbed(type, placeholders);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
        } else {
            event.replyEmbeds(embed).setEphemeral(true).queue();
        }
    }

    private void sendError(CommandErrorType type) {
        sendError(type, new HashMap<>());
    }
} 