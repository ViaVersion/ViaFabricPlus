# Developer API

ViaFabricPlus exposes events and utility functions so other mods can integrate with it.
If you want to include it in your project, keep in mind:

- Requires **Java 25** (see `jvm_version` in `gradle.properties`)
- Needs a **Fabric Loom** setup (since it's a Minecraft mod, not a standalone API library)

## Adding as a Dependency

If you only need the **public API**, include the `viafabricplus-api` artifact.
If you also want to access **internal features**, use `viafabricplus`.

### Kotlin (Gradle)

```kotlin
repositories {
    // https://github.com/ViaVersion/ViaFabricPlus/blob/ver/<version>/docs/DEVELOPER_API.md
    mavenCentral()
    maven("https://repo.viaversion.com")
}

dependencies {
    // Replace it with latest release
    runtimeOnly("com.viaversion:viafabricplus:x.x.x")
    compileOnly("com.viaversion:viafabricplus-api:x.x.x")
}
```

### Groovy (Gradle)

```groovy
repositories {
    mavenCentral()
    maven { name = "ViaVersion"; url = "https://repo.viaversion.com" }
}

dependencies {
    // Replace it with latest release
    runtimeOnly("com.viaversion:viafabricplus:x.x.x")
    compileOnly("com.viaversion:viafabricplus-api:x.x.x")
}
```

## Using the API

Everything is reached through a single entry point:

```java
final ViaFabricPlusAPI api = ViaFabricPlus.api();
```

`ViaFabricPlusAPI` is the only stable API surface other mods should use. Next to the mod's own metadata it hands
out four sub-APIs:

- `protocolTranslation()` – the target version, per-connection versions, ViaVersion's `UserConnection` and a
  listener for version changes
- `conversions()` – translating items between Minecraft and ViaVersion, and dummy user connections
- `limitations()` – whether an item, enchantment, effect or banner pattern exists in a given version
- `screens()` – opening the mod's own screens

`apiVersion()` is incremented on every meaningful API change, so you can check what you're running against.

```java
ViaFabricPlus.api().addChangeProtocolVersionListener((oldVersion, newVersion) -> {
    // Called whenever the target protocol version changes
});
```

> `ViaFabricPlus.getImpl()` and `ViaFabricPlusBase` are deprecated for removal. Every method on it forwards to its
> replacement on `ViaFabricPlusAPI`, so migrating is a rename.

## Entrypoint

Since your mod may load **after** ViaFabricPlus, hook into the loading cycle with a `ViaFabricPlusEntrypoint`
declared in your `fabric.mod.json` under the `viafabricplus` key:

```json
{
  "entrypoints": {
    "viafabricplus": [
      "com.example.ExampleEntrypoint"
    ]
  }
}
```

All of its methods are optional: `onPreLoading`, `onPostSettingsLoading`, `onPostProtocolTranslationLoading`,
`onPostRegistryLoading` and `onPostGameLoading`.

## Custom Settings

`api.settings()` gives access to the mod's own groups and lets you register your own, which then show up as their
own tab in the settings screen:

```java
final SettingGroup group = ViaFabricPlus.api().settings().register("example_settings.example");

final BooleanSetting enabled = group.registerBoolean("enabled", true);
final VersionedBooleanSetting legacyOnly = group.registerVersionedBoolean("legacy_only", andOlder(v1_12_2), false);
```

The group and its settings are named by translation keys, so the example above needs `example_settings.example`
and `example_settings.example.enabled` in your language files.

A `VersionedBooleanSetting` displays its version range in the screen, and its `isActive()` only returns `true`
while the target version is inside that range.
