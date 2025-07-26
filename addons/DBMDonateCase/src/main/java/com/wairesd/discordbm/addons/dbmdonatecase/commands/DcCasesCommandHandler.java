package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import java.util.Map;
import com.wairesd.discordbm.api.message.ResponseType;

public class DcCasesCommandHandler implements CommandHandler {
    private final DCAPI api;
    private final DBMAPI dbmApi;
    private final Messages messages;

    public DcCasesCommandHandler(DCAPI api, DBMAPI dbmApi, Messages messages) {
        this.api = api;
        this.dbmApi = dbmApi;
        this.messages = messages;
    }

    @Override
    public void handleCommand(String command, Map<String, String> opts, String reqId) {
        dbmApi.setResponseType(ResponseType.REPLY);
        try {
            MessageSender sender = dbmApi.getMessageSender();
            Map<String, CaseData> cases = api.getCaseManager().getMap();
            if (cases.isEmpty()) {
                sender.sendResponse(reqId, messages.get("no_cases"));
                return;
            }
            StringBuilder sb = new StringBuilder();
            cases.forEach((type, data) -> {
                String displayName = api.getCaseManager().getByType(type)
                    .map(def -> def.settings().displayName())
                    .orElse(data.caseDisplayName());
                if (displayName != null) {
                    displayName = displayName.replaceAll("[§&][0-9a-fk-or]", "");
                }
                sb.append(type).append(" - ").append(displayName).append("\n");
            });
            var embed = dbmApi.createEmbedBuilder()
                .setTitle(messages.get("cases_title"))
                .setDescription(sb.toString())
                .build();
            sender.sendResponse(reqId, embed);
        } finally {
            dbmApi.clearResponseType();
        }
    }
} 