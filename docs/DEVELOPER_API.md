# Developer API
ViaFabricPlus provides events and various utility functions for other mods to interface with it. Note that including
ViaFabricPlus in your project comes with some requirements:
- The target version is Java 17
- Fabric loom setup (As ViaFabricPlus is a Minecraft mod and has no API-only dependency like other projects)

## How to include the mod as dependency
If you are targetting to only use the provided API, you should include the ```viafabricplus-api``` artifact. For including the internals use
```viafabricplus```. Including the internals will also provide the legacy compatibility layer.

### Gradle
```groovy
repositories {
    mavenCentral()
    maven { 
        name = "ViaVersion"
        url = "https://repo.viaversion.com"
    }
    maven {
        name = "Lenni0451"
        url = "https://maven.lenni0451.net/everything"
    }
    maven {
        name = "Jitpack"
        url = "https://jitpack.io"

        content {
            includeGroup "com.github.Oryxel"
        }
    }
}

dependencies {
    modImplementation("com.viaversion:viafabricplus-api:x.x.x") // Get the latest version from releases
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>viaversion</id>
        <url>https://repo.viaversion.com</url>
    </repository>
    <repository>
        <id>lenni0451</id>
        <url>https://maven.lenni0451.net/everything</url>
    </repository>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.viaversion</groupId>
        <artifactId>viafabricplus-api</artifactId>
        <version>x.x.x</version> <!-- Get the latest version from releases -->
    </dependency>
</dependencies>
```

## Using the API
Get the general API using ``ViaFabricPlus.getImpl()`` which will return a ``ViaFabricPlusBase`` interface reflecting API functions for mods to use.
All functions provided there are safe to use and will most likely never be removed.

#### Example
```java
final ViaFabricPlusBase platform = ViaFabricPlus.getImpl();
```

## Using event callbacks

The API provides two event callbacks which can be used:
- ``LoadingCycleCallback`` fired in various loading stages of ViaFabricPlus such as config or settings loading.
- ``ChangeProtocolVersionCallback`` fired when the user changes the target version in the screen, or if the user joins a server with a different version.

Event callbacks can be registered through the API:

```java
final ViaFabricPlusBase platform = ViaFabricPlus.getImpl();

platform.registerOnChangeProtocolVersionCallback((oldVersion, newVersion) -> {
    // Called when the target version changes
});
```

### Using the loading cycle callback

As your mod might load after ViaFabricPlus, you will need to register the loading cycle callback inside a `ViaFabricPlusLoadEntrypoint` marked as `viafabricplus`
in your `fabric.mod.json` file. The entrypoint also acts as `INITAL_LOAD` stage.

```java
public class Example implements ViaFabricPlusLoadEntrypoint {

    @Override
    public void onPlatformLoad(ViaFabricPlusBase platform) {
        platform.registerLoadingCycleCallback(stage -> {
            if (stage == LoadingCycleCallback.LoadingCycle.PRE_SETTINGS_LOAD) {
                // Called before the settings are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_SETTINGS_LOAD) {
                // Called after the settings are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.PRE_FILES_LOAD) {
                // Called before the files are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_FILES_LOAD) {
                // Called after the files are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.PRE_VIAVERSION_LOAD) {
                // Called before ViaVersion is loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_VIAVERSION_LOAD) {
                // Called after ViaVersion is loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.FINAL_LOAD) {
                // Called after everything is loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_GAME_LOAD) {
                // Called after the game is loaded
            }
        });
    }
}
```

## More extensive API

For any version specific functionality, ViaFabricPlus provides common API functions. ViaFabricPlus uses [ViaVersion](https://github.com/ViaVersion/ViaVersion) 
for protocol translation, so the ViaVersion API can be used as well.

### Getting the current target version

```java
final ProtocolVersion version = ViaFabricPlus.getImpl().getTargetVersion();
```