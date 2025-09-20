# DiscordBM plugins for Minecraft Host/Client implementation

| [Wiki](https://1wairesd.github.io/1wairesdIndustriesWiki/docs/DiscordBM/dscordbm-main) |
|------------------------------------------------------------------------------------------|

<div style="background-color:#FFA500; color:white; padding:10px; border-radius:8px; text-align:center; font-weight:bold; font-size:1.2em;">
PROJECT IS CURRENTLY IN BETA VERSION
</div>

## API

> Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
  <groupId>com.github.wairesd</groupId>
  <artifactId>DiscordBM</artifactId>
  <version><PLATFORM>-1.0.1</version>
</dependency>
```
> Gradle
```groovy
maven {
    url = "https://jitpack.io"
}
```
```groovy
compileOnly("com.github.wairesd:DiscordBM:<PLATFORM>-1.0.1")
```

### Available Platforms
- `api` - Core API
- `bukkit-api` - Bukkit API
- `velocity-api` - Velocity API

### Usage Examples
```groovy
compileOnly("com.github.wairesd:DiscordBM:api-1.0.0")
```