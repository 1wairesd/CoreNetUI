package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import okhttp3.*;
import net.kyori.adventure.text.Component;
import com.google.gson.Gson;
import java.io.OutputStreamWriter;

public class ApplyEditsCommand {
    private static final String BYTEBIN_URL = "https://bytebin.lucko.me/";
    private final Path dataDirectory;

    public ApplyEditsCommand(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void execute(CommandSource source, String code) {
        if (code == null || code.isEmpty()) {
            source.sendMessage(Component.text("Не указан код!"));
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BYTEBIN_URL + code).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                source.sendMessage(Component.text("Ошибка загрузки данных с bytebin!"));
                return;
            }
            String json = response.body().string();
            Map<String, Object> map = new Gson().fromJson(json, Map.class);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);
            Path commandsPath = dataDirectory.resolve("commands.yml");
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(commandsPath), java.nio.charset.StandardCharsets.UTF_8)) {
                yaml.dump(map, writer);
            }
            source.sendMessage(Component.text("Изменения успешно применены!"));
        } catch (IOException e) {
            source.sendMessage(Component.text("Ошибка применения изменений: " + e.getMessage()));
        }
    }
} 