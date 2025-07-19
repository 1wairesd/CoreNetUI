package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import java.io.IOException;
import java.nio.file.Path;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.host.common.service.HostCommandService;

public class ApplyEditsCommand {
    private final Path dataDirectory;

    public ApplyEditsCommand(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void execute(CommandSource source, String code) {
        if (code == null || code.isEmpty()) {
            source.sendMessage(Component.text("Не указан код!"));
            return;
        }
        try {
            HostCommandService.applyEditsFromEditor(dataDirectory, code);
            source.sendMessage(Component.text("Изменения успешно применены!"));
        } catch (IOException e) {
            source.sendMessage(Component.text("Ошибка применения изменений: " + e.getMessage()));
        }
    }
} 