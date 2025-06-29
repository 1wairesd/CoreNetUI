package com.wairesd.discordbm.host.common.discord.selection;

import com.google.gson.Gson;
import com.wairesd.discordbm.host.common.config.configurators.Messages;
import com.wairesd.discordbm.host.common.discord.request.RequestSender;
import com.wairesd.discordbm.host.common.discord.response.ResponseHelper;
import com.wairesd.discordbm.host.common.models.request.RequestMessage;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerSelector {
    private static final String SELECT_MENU_PREFIX = "select_server_";

    private final ConcurrentHashMap<String, SelectionInfo> pendingSelections = new ConcurrentHashMap<>();

    private final RequestSender requestSender;
    private final ResponseHelper responseHelper;

    public ServerSelector(RequestSender requestSender, ResponseHelper responseHelper) {
        this.requestSender = requestSender;
        this.responseHelper = responseHelper;
    }

    public void sendServerSelectionMenu(SlashCommandInteractionEvent event, List<NettyServer.ServerInfo> servers) {
        String selectMenuId = generateSelectMenuId();
        pendingSelections.put(selectMenuId, new SelectionInfo(event, servers));

        StringSelectMenu menu = createServerSelectMenu(selectMenuId, servers);

        event.reply(Messages.get(Messages.Keys.SERVER_SELECTION_PROMPT))
                .addActionRow(menu)
                .setEphemeral(true)
                .queue();
    }

    public boolean isValidSelectMenu(StringSelectInteractionEvent event) {
        return event.getComponentId().startsWith(SELECT_MENU_PREFIX);
    }

    public void handleSelection(StringSelectInteractionEvent event) {
        SelectionInfo selectionInfo = pendingSelections.remove(event.getComponentId());
        if (selectionInfo == null) {
            responseHelper.replySelectionTimeout(event);
            return;
        }

        String chosenServerName = event.getValues().stream().findFirst().orElse(null);
        if (chosenServerName == null) {
            responseHelper.replyNoServerSelected(event);
            return;
        }

        NettyServer.ServerInfo targetServer = findTargetServer(selectionInfo.servers(), chosenServerName);
        if (targetServer == null) {
            responseHelper.replyServerNotFound(event);
            return;
        }

        event.deferEdit().queue(hook -> {
            SlashCommandInteractionEvent originalEvent = selectionInfo.event();
            String commandName = originalEvent.getName();

            UUID requestId = UUID.randomUUID();
            Map<String, String> options = originalEvent.getOptions().stream()
                    .collect(Collectors.toMap(opt -> opt.getName(), opt -> opt.getAsString()));
            
            RequestMessage request = new RequestMessage("request", commandName, options, requestId.toString());
            String json = new Gson().toJson(request);
            
            requestSender.storeInteractionHook(requestId, hook);
            requestSender.storeServerNameForRequest(requestId, chosenServerName);
            
            targetServer.channel().writeAndFlush(json);
            
            hook.editOriginal(Messages.get(Messages.Keys.SERVER_PROCESSING, chosenServerName)).queue();
        });
    }

    private NettyServer.ServerInfo findTargetServer(List<NettyServer.ServerInfo> servers, String chosenServerName) {
        return servers.stream()
                .filter(server -> server.serverName().equals(chosenServerName))
                .findFirst()
                .orElse(null);
    }

    private String generateSelectMenuId() {
        return SELECT_MENU_PREFIX + UUID.randomUUID();
    }

    private StringSelectMenu createServerSelectMenu(String selectMenuId, List<NettyServer.ServerInfo> servers) {
        List<SelectOption> options = servers.stream()
                .map(server -> SelectOption.of(server.serverName(), server.serverName()))
                .collect(Collectors.toList());

        return StringSelectMenu.create(selectMenuId)
                .setPlaceholder(Messages.get(Messages.Keys.SERVER_SELECTION_PLACEHOLDER))
                .setRequiredRange(1, 1)
                .addOptions(options)
                .build();
    }

    public record SelectionInfo(SlashCommandInteractionEvent event, List<NettyServer.ServerInfo> servers) {
    }
} 