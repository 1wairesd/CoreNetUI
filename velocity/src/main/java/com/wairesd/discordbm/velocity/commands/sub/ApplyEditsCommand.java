package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import java.io.IOException;
import java.nio.file.Path;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.host.common.service.HostCommandService;
import com.wairesd.discordbm.velocity.api.VelocityCommandSender;

public class ApplyEditsCommand {
    private final Path dataDirectory;

    public ApplyEditsCommand(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void execute(CommandSource source, String arg) {
        VelocityCommandSender sender = new VelocityCommandSender(source);
        if (arg == null || arg.isEmpty()) {
            sender.sendMessage(Component.text("Не указан код!"));
            return;
        }
        try {
            HostCommandService.applyEditsFromEditor(dataDirectory, arg);
            sender.sendMessage(Component.text("Изменения успешно применены!"));
        } catch (IOException e) {
            sender.sendMessage(Component.text("Ошибка применения изменений: " + e.getMessage()));
        }
    }
} 