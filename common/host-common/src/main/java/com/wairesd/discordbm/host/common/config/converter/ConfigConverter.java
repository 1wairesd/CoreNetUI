package com.wairesd.discordbm.host.common.config.converter;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ConfigConverter {
    private static final DumperOptions dumperOptions = new DumperOptions();
    
    static {
        dumperOptions.setIndent(2);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }
    
    public static Yaml createFormattedYaml() {
        return new Yaml(dumperOptions);
    }
}