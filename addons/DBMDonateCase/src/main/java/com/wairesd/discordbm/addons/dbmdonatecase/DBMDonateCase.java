package com.wairesd.discordbm.addons.dbmdonatecase;

import com.wairesd.discordbm.addons.dbmdonatecase.commands.*;
import com.jodexindustries.donatecase.api.DCAPI;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.Messages;
import com.wairesd.discordbm.addons.dbmdonatecase.configurators.WebhookTriggersConfig;
import com.wairesd.discordbm.api.DBMAPI;
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
    private DBMAPI dbmApi;
    private CaseKeyManager keyManager;
    private CaseOpenManager openManager;
    private Messages messages;
    private CaseOpenListener caseOpenListener;
    private DBMReloadListener listener = new DBMReloadListener(messages);
    private Object edcApi;
    private boolean edcAvailable = false;

    @Override
    public void onEnable() {
        this.api = DCAPI.getInstance();
        this.dbmApi = DBMAPI.getInstance();
        this.keyManager = api.getCaseKeyManager();
        this.openManager = api.getCaseOpenManager();
        this.messages = new Messages(this);

        try {
            Class<?> dailyCaseApiClass = Class.forName("com.wairesd.dceverydaycase.api.DailyCaseApi");
            this.edcApi = dailyCaseApiClass.getMethod("getInstance").invoke(null);
            this.edcAvailable = true;
        } catch (ClassNotFoundException e) {
            getLogger().warning("DCEveryDayCase API is not available: DailyCaseApi class not found");
            this.edcAvailable = false;
        } catch (Exception e) {
            getLogger().warning("Error initializing DCEveryDayCase API: " + e.getMessage());
            this.edcAvailable = false;
        }


        WebhookTriggersConfig triggersConfig = new WebhookTriggersConfig(this);
        caseOpenListener = new CaseOpenListener(
            dbmApi,
            getLogger(),
            triggersConfig,
            openManager,
            api.getCaseManager()
        );
        api.getEventBus().register(caseOpenListener);
        dbmApi.getEventBus().register(listener);

        CommandOption playerOpt = dbmApi.getCommandRegistration().createOptionBuilder()
            .name("player")
            .description(messages.get("cmd_player_option"))
            .type("STRING")
            .required(true)
            .build();

        Command dcStatsCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dcstats")
            .description(messages.get("cmd_dcstats_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .options(List.of(playerOpt))
            .build();

        dbmApi.getCommandRegistration().registerCommand(dcStatsCmd, new DcStatsCommandHandler(api, dbmApi, keyManager, openManager, messages));

        Command dcCasesCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dccases")
            .description(messages.get("cmd_dccases_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcCasesCmd, new DcCasesCommandHandler(api, dbmApi, messages));

        Command dcTopCasesCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dctopcases")
            .description(messages.get("cmd_dctopcases_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcTopCasesCmd, new DcTopCasesCommandHandler(api, dbmApi, messages));

        CommandOption caseOpt = dbmApi.getCommandRegistration().createOptionBuilder()
            .name("case")
            .description(messages.get("cmd_case_type_option"))
            .type("STRING")
            .required(true)
            .build();
        Command dcTopPlayersCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dctopplayers")
            .description(messages.get("cmd_dctopplayers_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .options(List.of(caseOpt))
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcTopPlayersCmd, new DcTopPlayersCommandHandler(api, dbmApi, messages));

        Command dcTopPlayersAllCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dctopplayersall")
            .description(messages.get("cmd_dctopplayersall_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcTopPlayersAllCmd, new DcTopPlayersAllCommandHandler(api, dbmApi, messages));

        Command dcLastDropsCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dclastdrops")
            .description(messages.get("cmd_dclastdrops_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcLastDropsCmd, new DcLastDropsCommandHandler(api, dbmApi, messages));

        CommandOption playerOptHistory = dbmApi.getCommandRegistration().createOptionBuilder()
            .name("player")
            .description(messages.get("cmd_player_option"))
            .type("STRING")
            .required(true)
            .build();
        Command dcHistoryCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dchistory")
            .description(messages.get("cmd_dchistory_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .options(List.of(playerOptHistory))
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcHistoryCmd, new DcHistoryCommandHandler(api, dbmApi, messages));

        Command dcHelpCmd = dbmApi.getCommandRegistration().createCommandBuilder()
            .name("dchelp")
            .description(messages.get("cmd_dchelp_description"))
            .pluginName("DBMDonateCase")
            .context("both")
            .build();
        dbmApi.getCommandRegistration().registerCommand(dcHelpCmd, (cmd, options, requestId) -> {
            StringBuilder helpText = new StringBuilder();
            helpText.append(messages.get("dchelp_list"));
            Embed embed = dbmApi.createEmbedBuilder()
                .setTitle(messages.get("dchelp_title"))
                .setDescription(helpText.toString())
                .build();
            dbmApi.getMessageSender().sendResponse(requestId, embed);
        });

        if (edcAvailable && edcApi != null) {
            try {
                CommandOption edcPlayerOpt = dbmApi.getCommandRegistration().createOptionBuilder()
                    .name("player")
                    .description(messages.get("cmd_player_option"))
                    .type("STRING")
                    .required(true)
                    .build();

                Command edcStatsCmd = dbmApi.getCommandRegistration().createCommandBuilder()
                    .name("edcstats")
                    .description(messages.get("cmd_edcstats_description"))
                    .pluginName("DBMDonateCase")
                    .context("both")
                    .options(List.of(edcPlayerOpt))
                    .build();
                dbmApi.getCommandRegistration().registerCommand(edcStatsCmd, new EdcStatsCommandHandler(edcApi, dbmApi, messages));

                Command edcTopPlayersCmd = dbmApi.getCommandRegistration().createCommandBuilder()
                    .name("edctopplayers")
                    .description(messages.get("cmd_edctopplayers_description"))
                    .pluginName("DBMDonateCase")
                    .context("both")
                    .build();
                dbmApi.getCommandRegistration().registerCommand(edcTopPlayersCmd, new EdcTopPlayersCommandHandler(edcApi, dbmApi, messages));

                getLogger().info("EDC commands registered: edcstats, edctopplayers");
            } catch (Exception e) {
                getLogger().warning("Failed to register EDC commands: " + e.getMessage());
            }
        } else {
            getLogger().warning("EDC commands not registered - DCEveryDayCase is not available");
        }

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

        if (edcAvailable) {
            dbmApi.getCommandRegistration().unregisterCommand("edcstats", getName());
            dbmApi.getCommandRegistration().unregisterCommand("edctopplayers", getName());
        }
        
        dbmApi.getEventBus().unregister(listener);
        api.getEventBus().unregister(caseOpenListener);
        getLogger().info("DBMDonateCase is disabled!");
    }
}
