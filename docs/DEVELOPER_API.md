# Developer API
ViaFabricPlus provides events and various utility functions for other mods to interface with it. Note that including
ViaFabricPlus in your project comes with some requirements:
- The target version is Java 17
- Fabric loom setup (As ViaFabricPlus is a Minecraft mod and has no API-only dependency like other projects)

Since the API is not exposed as standalone submodule (yet), functions that shouldn't be used are marked with
`ApiStatus.Internal`. Further information about certain functions can be found in the Javadoc in the corresponding file.

## How to include the mod as dependency
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
    modImplementation("de.florianmichael:ViaFabricPlus:x.x.x") // Get the latest version from releases
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
        <groupId>de.florianmichael</groupId>
        <artifactId>ViaFabricPlus</artifactId>
        <version>x.x.x</version> <!-- Get the latest version from releases -->
    </dependency>
</dependencies>
```

## Interacting with Events
ViaFabricPlus events are the intended way of interacting with the mod.
Events are fired in various situations and are using the [Fabric Event API](https://fabricmc.net/wiki/tutorial:events). 

#### Example
```java
ChangeProtocolVersionCallback.EVENT.register((oldVersion, newVersion) -> {
    System.out.println("Version changed to " + newVersion.getName());
});
```
### List of events/callbacks
| Callback class name                  | Description                                                                                                                                                                                                   |
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ChangeProtocolVersionCallback        | Called when the user changes the target version in the screen, or if you connect to a server for which a specific version has been selected, you disconnect, the event for the actual version is also called. |
| DisconnectCallback                   | Called when the user disconnects from a server                                                                                                                                                                |
| LoadCallback                         | Called at the earliest point ViaFabricPlus is injecting too                                                                                                                                                   |
| LoadClassicProtocolExtensionCallback | Called when the classic server sends the protocol extensions (only in **c0.30 CPE**)                                                                                                                          |
| PostGameLoadCallback                 | Called when Minecraft is finished with loading all its components                                                                                                                                             |
| PostViaVersionLoadCallback           | Called when ViaVersion is loaded and ready to use                                                                                                                                                             |
| RegisterSettingsCallback             | Called after the default setting groups are loaded and before the setting config is loaded                                                                                                                    |
| LoadSaveFilesCallback                | Called before and after the save files are loaded                                                                                                                                                             |

### Other API calls

ViaFabricPlus uses [ViaVersion](https://github.com/ViaVersion/ViaVersion) for protocol translation, so the ViaVersion API can be used.

The general API endpoint for ViaFabricPlus specifics is `ProtocolTranslator` 
```java
// Get and change the current selected version
final ProtocolVersion version = ProtocolTranslator.getTargetVersion();
if (version == ProtocolVersion.v1_8) {
    ProtocolTranslator.setTargetVersion(ProtocolVersion.v1_9);
}

// Gets the ViaVersion user connection object for raw packet sending using ViaVersion API
final UserConnection user = ProtocolTranslator.getPlayNetworkUserConnection();
if (user == null) {
    // Mod not active
}
```