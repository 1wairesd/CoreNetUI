
package com.wairesd.discordbm.velocity.config;

import com.wairesd.discordbm.velocity.config.configurators.Commands;
import com.wairesd.discordbm.velocity.config.configurators.Forms;
import com.wairesd.discordbm.velocity.config.configurators.Messages;
import com.wairesd.discordbm.velocity.config.configurators.Settings;

import java.nio.file.Path;

public class ConfigManager {

    public static void init(Path dataDir) {
        Settings.init(dataDir.toFile());
        Messages.init(dataDir);
        Commands.init(dataDir);
        Forms.init(dataDir.toFile());
    }

    public static void ConfigureReload() {
        Settings.reload();
        Messages.reload();
        Commands.reload();
        Forms.reload();
    }
}