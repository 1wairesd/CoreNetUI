package com.wairesd.discordbm.velocity.models.register;

import com.wairesd.discordbm.velocity.models.command.CommandDefinition;

import java.util.List;

public record RegisterMessage(String type, String serverName, String pluginName, List<CommandDefinition> commands, String secret) {}