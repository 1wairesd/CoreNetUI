package com.wairesd.discordbm.host.common.models.command;

import com.wairesd.discordbm.host.common.models.option.OptionDefinition;

import java.util.List;

public record CommandDefinition(String name, String description, String context, List<OptionDefinition> options) {}