package com.wairesd.discordbm.host.common.commandbuilder.commands.core;

import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.commands.processor.CommandBuilder;
import com.wairesd.discordbm.host.common.commandbuilder.commands.processor.CommandDefinitionRegistrar;
import com.wairesd.discordbm.host.common.commandbuilder.commands.processor.CommandLoader;
import com.wairesd.discordbm.host.common.commandbuilder.commands.processor.CommandRegistrar;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.structures.CommandStructured;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.JDA;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wairesd.discordbm.host.common.config.configurators.Settings.isDebugCommandRegistrations;

public class CommandManager {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBMV"));
    private final Map<String, CommandStructured> commandMap = new HashMap<>();

    private final CommandLoader loader = new CommandLoader();
    private final CommandRegistrar registrar;
    private final CommandDefinitionRegistrar defRegistrar;

    public CommandManager(NettyServer netty, JDA jda) {
        this.registrar = new CommandRegistrar(jda, new CommandBuilder());
        this.defRegistrar = new CommandDefinitionRegistrar(netty);
    }

    public void loadAndRegisterCommands() {
        List<CommandStructured> commands = loader.load();
        commandMap.clear();
        int count = 0;

        for (CommandStructured cmd : commands) {
            if (cmd == null) continue;
            if (registrar.register(cmd)) {
                defRegistrar.register(cmd);
                commandMap.put(cmd.getName(), cmd);
                count++;
            }
        }

        if (isDebugCommandRegistrations()) {
            logger.info("Registered {} commands", count);
        }

    }

    public CommandStructured getCommand(String name) {
        return commandMap.get(name);
    }
}
