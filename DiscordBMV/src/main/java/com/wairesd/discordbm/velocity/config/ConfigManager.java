
package com.wairesd.discordbm.velocity.config;

import java.nio.file.Path;
import com.wairesd.discordbm.velocity.config.configurators.Commands;
import com.wairesd.discordbm.velocity.config.configurators.Messages;
import com.wairesd.discordbm.velocity.config.configurators.Settings;

public class ConfigManager {

    public static void init(Path dataDir) {
        Settings.init(dataDir);
        Messages.init(dataDir);
        Commands.init(dataDir);
    }

    public static void ConfigureReload() {
        Settings.reload();
        Messages.reload();
        Commands.reload();
    }
}
