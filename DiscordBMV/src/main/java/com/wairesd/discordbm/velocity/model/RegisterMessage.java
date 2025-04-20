package com.wairesd.discordbm.velocity.model;

import java.util.List;

public record RegisterMessage(String type, String serverName, String pluginName, List<CommandDefinition> commands, String secret) {}