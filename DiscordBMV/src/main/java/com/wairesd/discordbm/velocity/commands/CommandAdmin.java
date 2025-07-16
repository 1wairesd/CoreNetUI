package com.wairesd.discordbm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.wairesd.discordbm.common.utils.color.ColorUtils;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.velocity.commands.sub.ReloadCommand;
import com.wairesd.discordbm.velocity.commands.sub.CommandsCommand;
import com.wairesd.discordbm.velocity.commands.sub.HelpCommand;
import com.wairesd.discordbm.velocity.commands.sub.ClientsCommand;
import com.wairesd.discordbm.common.utils.color.MessageContext;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.velocity.commands.sub.WebHookCommand;
import com.wairesd.discordbm.velocity.DiscordBMV;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.common.utils.color.transform.AnsiColorTranslator;
import com.wairesd.discordbm.velocity.commands.sub.EditorCommand;
import com.wairesd.discordbm.velocity.commands.sub.ApplyEditsCommand;

public class CommandAdmin implements SimpleCommand {
    private final ReloadCommand reloadCommand;
    private final CommandsCommand commandsCommand;
    private final HelpCommand helpCommand;
    private final ClientsCommand clientsCommand;
    private final DiscordBMHPlatformManager platformManager;
    private final WebHookCommand webHookCommand;
    private final java.nio.file.Path dataDirectory;
    private final EditorCommand editorCommand;
    private final ApplyEditsCommand applyEditsCommand;

    public CommandAdmin(DiscordBMHPlatformManager platformManager) {
        this.platformManager = platformManager;
        this.dataDirectory = DiscordBMV.plugin.getDataDirectory();
        this.reloadCommand = new ReloadCommand(platformManager);
        this.commandsCommand = new CommandsCommand(platformManager);
        this.helpCommand = new HelpCommand();
        this.clientsCommand = new ClientsCommand(platformManager);
        this.webHookCommand = new WebHookCommand();
        this.editorCommand = new EditorCommand(dataDirectory);
        this.applyEditsCommand = new ApplyEditsCommand(dataDirectory);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        MessageContext context = (source instanceof ConsoleCommandSource) ? MessageContext.CONSOLE : MessageContext.CHAT;

        if (args.length == 0) {
            if (context == MessageContext.CONSOLE) {
                source.sendMessage(Component.text(AnsiColorTranslator.translate(HostCommandService.getHelp(context))));
            } else {
                source.sendMessage(ColorUtils.parseComponent(HostCommandService.getHelp(context)));
            }
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reloadCommand.execute(source, context, platformManager, dataDirectory);
            case "commands" -> commandsCommand.execute(source, args, context, platformManager);
            case "clients" -> clientsCommand.execute(source, context, platformManager);
            case "help" -> helpCommand.execute(source, context);
            case "webhook" -> webHookCommand.execute(source, args, context, dataDirectory);
            case "editor" -> editorCommand.execute(source);
            case "applyedits" -> {
                if (args.length < 2) {
                    source.sendMessage(Component.text("Укажите код изменений!"));
                } else {
                    applyEditsCommand.execute(source, args[1]);
                }
            }
            default -> {
                if (context == MessageContext.CONSOLE) {
                    source.sendMessage(Component.text(AnsiColorTranslator.translate(HostCommandService.getHelp(context))));
                } else {
                    source.sendMessage(ColorUtils.parseComponent(HostCommandService.getHelp(context)));
                }
            }
        }
    }
}
