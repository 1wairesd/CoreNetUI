package com.wairesd.discordbm.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.wairesd.discordbm.velocity.DiscordBMV;
import com.wairesd.discordbm.velocity.commands.sub.ReloadCommand;
import com.wairesd.discordbm.velocity.commands.sub.CommandsCommand;
import com.wairesd.discordbm.velocity.config.configurators.Messages;

public class CommandAdmin implements SimpleCommand {
    private final ReloadCommand reloadCommand;
    private final CommandsCommand commandsCommand;

    public CommandAdmin(DiscordBMV plugin) {
        this.reloadCommand = new ReloadCommand(plugin);
        this.commandsCommand = new CommandsCommand(plugin);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Messages.getParsedMessage("usage-admin-command", null));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reloadCommand.execute(source);
            case "commands" -> commandsCommand.execute(source);
            default -> source.sendMessage(Messages.getParsedMessage("usage-admin-command", null));
        }
    }
}
