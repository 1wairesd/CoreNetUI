package com.wairesd.discordbm.addons.dbmdonatecase;

import com.wairesd.discordbm.addons.dbmdonatecase.commands.*;
import com.jodexindustries.donatecase.api.DCAPI;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.WebhookTriggersConfig;
import com.wairesd.discordbm.api.DiscordBMAPI;
import com.wairesd.discordbm.api.DiscordBMAPIProvider;
import com.wairesd.discordbm.api.command.Command;
import com.wairesd.discordbm.api.command.CommandOption;
import com.jodexindustries.donatecase.api.manager.CaseKeyManager;
import com.jodexindustries.donatecase.api.manager.CaseOpenManager;
import com.wairesd.discordbm.addons.dbmdonatecase.listener.CaseOpenListener;
import com.wairesd.discordbm.addons.dbmdonatecase.listener.DBMReloadListener;
import com.wairesd.discordbm.api.embed.Embed;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public final class DBMDonateCase extends JavaPlugin {
    private DCAPI api;
    private DiscordBMAPI dbmApi;
    private CaseKeyManager keyManager;
    private CaseOpenManager openManager;
    private Messages messages;
    private CaseOpenListener caseOpenListener;

    @Override
    public void onEnable() {
        this.api = DCAPI.getInstance();
        this.dbmApi = DiscordBMAPIProvider.getInstanceOrThrow();
        this.keyManager = api.getCaseKeyManager();
        this.openManager = api.getCaseOpenManager();
        this.messages = new Messages(this);

        WebhookTriggersConfig triggersConfig = new WebhookTriggersConfig(this);
        caseOpenListener = new CaseOpenListener(
            dbmApi,
            getLogger(),
            triggersConfig,
            openManager,
            api.getCaseManager()
        );
        api.getEventBus().register(caseOpenListener);
        dbmApi.getEventBus().register(new DBMReloadListener(messages));

        CommandOption playerOpt = dbmApi.getCommandRegistration().createOptionBuilder()
            .name("player")
            .description("Ник игрока в майнкрафте")
            .type("STRING")
            .required(true)
            .build();

        Command dcStatsCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dcstats")
            .description("Показать количество ключей DonateCase у игрока")
            .pluginName("DBMDonateCase")
            .context("both")
            .options(List.of(playerOpt))
            .build();

        dbmApi.getCommandRegistration().registerCommand(dcStatsCmd, new DcStatsCommandHandler(api, dbmApi, keyManager, openManager, messages));

        Command dcCasesCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dccases")
            .description("Список всех кейсов DonateCase")
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcCasesCmd, new DcCasesCommandHandler(api, dbmApi, messages));

        Command dcTopCasesCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dctopcases")
            .description("Топ 10 кейсов по количеству открытий")
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcTopCasesCmd, new DcTopCasesCommandHandler(api, dbmApi, messages));

        CommandOption caseOpt = dbmApi.getCommandRegistration().createOptionBuilder()
            .name("case")
            .description("Тип кейса (casetype)")
            .type("STRING")
            .required(true)
            .build();
        Command dcTopPlayersCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dctopplayers")
            .description("Топ 10 игроков по открытию выбранного кейса")
            .pluginName("DBMDonateCase")
            .context("both")
            .options(List.of(caseOpt))
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcTopPlayersCmd, new DcTopPlayersCommandHandler(api, dbmApi, messages));

        Command dcTopPlayersAllCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dctopplayersall")
            .description("Топ 10 игроков по всем открытым кейсам")
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcTopPlayersAllCmd, new DcTopPlayersAllCommandHandler(api, dbmApi, messages));

        Command dcLastDropsCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dclastdrops")
            .description("Показать последние 10 открытий кейсов на сервере (кто, что, когда)")
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcLastDropsCmd, new DcLastDropsCommandHandler(api, dbmApi, messages));

        CommandOption playerOptHistory = dbmApi.getCommandRegistration().createOptionBuilder()
            .name("player")
            .description("Ник игрока в майнкрафте")
            .type("STRING")
            .required(true)
            .build();
        Command dcHistoryCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dchistory")
            .description("Показать последние 10 открытий кейсов игроком (что и когда выпало, какой кейс)")
            .pluginName("DBMDonateCase")
            .context("both")
            .options(List.of(playerOptHistory))
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcHistoryCmd, new DcHistoryCommandHandler(api, dbmApi, messages));

        Command dcHelpCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dchelp")
            .description("Показать все команды DBMDonateCase")
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcHelpCmd, (cmd, options, requestId) -> {
            Embed embed = dbmApi.createEmbedBuilder()
                .setTitle(messages.get("dchelp_title"))
                .setDescription(messages.get("dchelp_list"))
                .build();
            dbmApi.getMessageSender().sendResponse(requestId, embed);
        });

        getLogger().info("DBMDonateCase is enabled!");
    }

    @Override
    public void onDisable() {
        dbmApi.getCommandRegistration().unregisterCommand("dcstats", getName());
        dbmApi.getCommandRegistration().unregisterCommand("dccases", getName());
        dbmApi.getCommandRegistration().unregisterCommand("dctopcases", getName());
        dbmApi.getCommandRegistration().unregisterCommand("dctopplayers", getName());
        dbmApi.getCommandRegistration().unregisterCommand("dctopplayersall", getName());
        dbmApi.getCommandRegistration().unregisterCommand("dclastdrops", getName());
        dbmApi.getCommandRegistration().unregisterCommand("dchistory", getName());
        dbmApi.getCommandRegistration().unregisterCommand("dchelp", getName());
        if (caseOpenListener != null) {
            api.getEventBus().unregister(caseOpenListener);
        }
        getLogger().info("DBMDonateCase is disabled!");
    }
}
