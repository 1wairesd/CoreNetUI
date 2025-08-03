package com.wairesd.discordbm.velocity.libraries;

import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import org.slf4j.Logger;

import java.nio.file.Path;

public class LibraryLoader {
    private final ProxyServer proxy;
    private final PluginManager pluginManager;
    private final Path dataDirectory;
    private final Logger logger;
    private final Object plugin;

    public LibraryLoader(ProxyServer proxy, PluginManager pluginManager, Path dataDirectory, Logger logger, Object plugin) {
        this.proxy = proxy;
        this.pluginManager = pluginManager;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.plugin = plugin;
    }

    public void loadLibraries() {
        try {
            VelocityLibraryManager libraryManager = new VelocityLibraryManager(
                logger, dataDirectory, pluginManager, plugin
            );
            libraryManager.addMavenCentral();
            libraryManager.addRepository("https://repo1.maven.org/maven2/");
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.google.code.gson")
                    .artifactId("gson")
                    .version("2.10.1")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("io.netty")
                    .artifactId("netty-all")
                    .version("4.2.2.Final")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("net.dv8tion")
                    .artifactId("JDA")
                    .version("5.0.0-beta.21")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.squareup.okhttp3")
                    .artifactId("okhttp")
                    .version("4.12.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("net.sf.trove4j")
                    .artifactId("trove4j")
                    .version("3.0.3")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("org.apache.commons")
                    .artifactId("commons-collections4")
                    .version("4.5.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.neovisionaries")
                    .artifactId("nv-websocket-client")
                    .version("2.14")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.fasterxml.jackson.core")
                    .artifactId("jackson-core")
                    .version("2.15.3")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.fasterxml.jackson.core")
                    .artifactId("jackson-databind")
                    .version("2.15.3")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.fasterxml.jackson.core")
                    .artifactId("jackson-annotations")
                    .version("2.15.3")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.j256.ormlite")
                    .artifactId("ormlite-core")
                    .version("6.1")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.j256.ormlite")
                    .artifactId("ormlite-jdbc")
                    .version("6.1")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.mysql")
                    .artifactId("mysql-connector-j")
                    .version("9.3.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("org.xerial")
                    .artifactId("sqlite-jdbc")
                    .version("3.50.1.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("org.slf4j")
                    .artifactId("slf4j-api")
                    .version("2.0.17")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("org.apache.commons")
                    .artifactId("commons-lang3")
                    .version("3.17.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("org.apache.commons")
                    .artifactId("commons-collections4")
                    .version("4.5.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("org.jetbrains.kotlin")
                    .artifactId("kotlin-stdlib")
                    .version("1.9.23")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.squareup.okio")
                    .artifactId("okio")
                    .version("2.10.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("com.google.guava")
                    .artifactId("guava")
                    .version("33.4.8-jre")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("net.kyori")
                    .artifactId("event-method")
                    .version("3.0.0")
                    .build());
            libraryManager.loadLibrary(Library.builder()
                    .groupId("net.kyori")
                    .artifactId("event-api")
                    .version("3.0.0")
                    .build());
        } catch (Exception e) {
            logger.error("[LibraryLoader] Failed to load libraries", e);
        }
    }
}