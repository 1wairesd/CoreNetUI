package com.wairesd.discordbm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.sub.ReloadCommand;
import com.wairesd.discordbm.velocity.commands.sub.CommandsCommand;
import com.wairesd.discordbm.velocity.commands.sub.HelpCommand;
import com.wairesd.discordbm.velocity.config.configurators.Messages;
import com.wairesd.discordbm.common.utils.color.MessageContext;

public class CommandAdmin implements SimpleCommand {
    private final ReloadCommand reloadCommand;
    private final CommandsCommand commandsCommand;
    private final HelpCommand helpCommand;

    public CommandAdmin(DiscordBMV plugin) {
        this.reloadCommand = new ReloadCommand(plugin);
        this.commandsCommand = new CommandsCommand(plugin);
        this.helpCommand = new HelpCommand();
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        MessageContext context = (source instanceof ConsoleCommandSource) ? MessageContext.CONSOLE : MessageContext.CHAT;

        if (args.length == 0) {
            showHelp(source, context);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reloadCommand.execute(source, context);
            case "commands" -> commandsCommand.execute(source, args, context);
            case "help" -> helpCommand.execute(source, context);
            default -> showHelp(source, context);
        }
    }

    private void showHelp(CommandSource source, MessageContext context) {
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_HEADER, context));
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_RELOAD, context));
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_CUSTOM_COMMANDS, context));
        source.sendMessage(Messages.getComponent(Messages.Keys.HELP_ADDONS_COMMANDS, context));
    }
}
