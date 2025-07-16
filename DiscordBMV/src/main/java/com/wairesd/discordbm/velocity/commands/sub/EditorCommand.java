package com.wairesd.discordbm.velocity.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import okhttp3.*;
import net.kyori.adventure.text.Component;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class EditorCommand {
    private static final String BYTEBIN_URL = "https://bytebin.lucko.me/post";
    private final Path dataDirectory;

    public EditorCommand(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void execute(CommandSource source) {
        Path commandsPath = dataDirectory.resolve("commands.yml");
        if (!Files.exists(commandsPath)) {
            source.sendMessage(Component.text("commands.yml not found!"));
            return;
        }
        try (InputStream in = Files.newInputStream(commandsPath)) {
            Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
            Object loaded = yaml.load(in);
            if (!(loaded instanceof Map<?, ?> map)) {
                source.sendMessage(Component.text("Invalid YAML format!"));
                return;
            }
            String json = new Gson().toJson(map);
            byte[] gzipped;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 GZIPOutputStream gzip = new GZIPOutputStream(baos);
                 OutputStreamWriter writer = new OutputStreamWriter(gzip, StandardCharsets.UTF_8)) {
                writer.write(json);
                writer.flush();
                gzip.finish();
                gzipped = baos.toByteArray();
            }
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(gzipped, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                .url(BYTEBIN_URL)
                .addHeader("Content-Encoding", "gzip")
                .post(body)
                .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    source.sendMessage(Component.text("Ошибка отправки на bytebin!"));
                    return;
                }
                String resp = response.body().string();
                String key = JsonParser.parseString(resp).getAsJsonObject().get("key").getAsString();
                String url = "https://discordbmeditor.onrender.com/#" + key;
                source.sendMessage(Component.text("Откройте редактор: " + url));
            }
        } catch (IOException e) {
            source.sendMessage(Component.text("Ошибка чтения файла: " + e.getMessage()));
        }
    }
} 