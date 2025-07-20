package com.wairesd.discordbm.addons.dbmdonatecase.commands;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.data.casedata.CaseData;
import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.api.command.CommandHandler;
import com.wairesd.discordbm.api.message.MessageSender;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import java.util.Map;

public class DcCasesCommandHandler implements CommandHandler {
    private final DCAPI api;
    private final DiscordBMAPI dbmApi;
    private final Messages messages;

    public DcCasesCommandHandler(DCAPI api, DiscordBMAPI dbmApi, Messages messages) {
        this.api = api;
        this.dbmApi = dbmApi;
        this.messages = messages;
    }

    @Override
    public void handleCommand(String command, Map<String, String> opts, String reqId) {
        MessageSender sender = dbmApi.getMessageSender();
        Map<String, CaseData> cases = api.getCaseManager().getMap();
        if (cases.isEmpty()) {
            sender.sendResponse(reqId, messages.get("no_cases"));
            return;
        }
        StringBuilder sb = new StringBuilder();
        cases.forEach((type, data) -> sb.append(type).append("\n"));
        var embed = dbmApi.createEmbedBuilder()
            .setTitle(messages.get("cases_title"))
            .setDescription(sb.toString())
            .build();
        sender.sendResponse(reqId, embed);
    }
} 