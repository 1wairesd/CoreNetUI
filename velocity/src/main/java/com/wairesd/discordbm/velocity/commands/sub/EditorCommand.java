package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import java.io.IOException;
import java.nio.file.Path;
import net.kyori.adventure.text.Component;
import com.wairesd.discordbm.host.common.service.HostCommandService;

public class EditorCommand {
    private final Path dataDirectory;

    public EditorCommand(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void execute(CommandSource source) {
        try {
            String url = HostCommandService.uploadCommandsToEditor(dataDirectory);
            source.sendMessage(Component.text("Откройте редактор: " + url));
        } catch (IOException e) {
            source.sendMessage(Component.text("Ошибка: " + e.getMessage()));
        }
    }
} 