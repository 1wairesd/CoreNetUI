package com.wairesd.discordbm.velocity.model;

import java.util.List;

public record CommandDefinition(String name, String description, String context, List<OptionDefinition> options) {}