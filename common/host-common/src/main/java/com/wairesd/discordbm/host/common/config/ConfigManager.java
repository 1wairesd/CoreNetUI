
package com.wairesd.discordbm.host.common.config;

import com.wairesd.discordbm.host.common.config.configurators.*;
import com.wairesd.discordbm.host.common.config.configurators.*;

import java.nio.file.Path;

public class ConfigManager {

    public static void init(Path dataDir) {
        Settings.init(dataDir.toFile());
        Messages.init(dataDir);
        Commands.init(dataDir);
        Forms.init(dataDir.toFile());
        Pages.init(dataDir.toFile());
    }

    public static void ConfigureReload() {
        Settings.reload();
        Messages.reload();
        Commands.reload();
        Forms.reload();
        Pages.reload();
    }
}