# Developer API

ViaFabricPlus exposes events and utility functions so other mods can integrate with it.
If you want to include it in your project, keep in mind:

- Requires **Java 17**
- Needs a **Fabric Loom** setup (since it’s a Minecraft mod, not a standalone API library)

---

## Adding as a Dependency

If you only need the **public API**, include the `viafabricplus-api` artifact.
If you also want to access **internal features** (including the legacy compatibility layer), use `viafabricplus`.

### Kotlin (Gradle)

```kotlin
repositories {
    maven("https://repo.viaversion.com")
    maven("https://maven.lenni0451.net/everything")
    maven("https://repo.opencollab.dev/maven-snapshots")
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.Oryxel")
        }
    }
}

dependencies {
    modImplementation("com.viaversion:viafabricplus-api:x.x.x") // Replace with latest release
}
```

### Groovy (Gradle)

```groovy
repositories {
    mavenCentral()
    maven { name = "ViaVersion"; url = "https://repo.viaversion.com" }
    maven { name = "Lenni0451"; url = "https://maven.lenni0451.net/everything" }
    maven { name = "OpenCollab"; url = "https://repo.opencollab.dev/maven-snapshots" }
    maven {
        name = "Jitpack"
        url = "https://jitpack.io"
        content { includeGroup "com.github.Oryxel" }
    }
}

dependencies {
    modImplementation("com.viaversion:viafabricplus-api:x.x.x") // Check latest version in releases
}
```

---

## Using the API

Get the main API instance with:

```java
final ViaFabricPlusBase platform = ViaFabricPlus.getImpl();
```

The `ViaFabricPlusBase` interface contains all stable API functions that other mods can safely use.

---

## Event Callbacks

ViaFabricPlus provides two key callbacks:

* **`LoadingCycleCallback`** – Fired at different stages of ViaFabricPlus loading (config, settings, files, etc.)
* **`ChangeProtocolVersionCallback`** – Fired when the user switches to another protocol version (manually or by joining a server)

### Example: Listening for version changes

```java
final ViaFabricPlusBase platform = ViaFabricPlus.getImpl();

platform.registerOnChangeProtocolVersionCallback((oldVersion, newVersion) -> {
    // Called whenever the target protocol version changes
});
```

---

## Loading Cycle Callback

Since your mod may load **after** ViaFabricPlus, you need to register callbacks inside a `ViaFabricPlusLoadEntrypoint` declared in your `fabric.mod.json` as `viafabricplus`.
This entrypoint also represents the `INITIAL_LOAD` stage.

```java
public class Example implements ViaFabricPlusLoadEntrypoint {

    @Override
    public void onPlatformLoad(ViaFabricPlusBase platform) {
        platform.registerLoadingCycleCallback(stage -> {
            if (stage == LoadingCycleCallback.LoadingCycle.PRE_SETTINGS_LOAD) {
                // Before settings are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_SETTINGS_LOAD) {
                // After settings are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.PRE_FILES_LOAD) {
                // Before files are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_FILES_LOAD) {
                // After files are loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.PRE_VIAVERSION_LOAD) {
                // Before ViaVersion initializes
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_VIAVERSION_LOAD) {
                // After ViaVersion initializes
            } else if (stage == LoadingCycleCallback.LoadingCycle.FINAL_LOAD) {
                // After everything is fully loaded
            } else if (stage == LoadingCycleCallback.LoadingCycle.POST_GAME_LOAD) {
                // After the game finishes loading
            }
        });
    }
}
```

---

## Extended API

For version-specific tasks, ViaFabricPlus provides a range of helper methods.
Since it relies on [ViaVersion](https://github.com/ViaVersion/ViaVersion), you can also use the ViaVersion API directly.

### Example: Get current protocol version

```java
final ProtocolVersion version = ViaFabricPlus.getImpl().getTargetVersion();
```
