package com.wairesd.discordbm.velocity.models.command;

import com.wairesd.discordbm.velocity.models.option.OptionDefinition;

import java.util.List;

public record CommandDefinition(String name, String description, String context, List<OptionDefinition> options) {}