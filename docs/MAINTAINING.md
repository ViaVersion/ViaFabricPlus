# Updating instructions for the project

These steps are the usual process for updating ViaFabricPlus to a new version of the game. If you're unsure about
something, ask in the ViaVersion discord.

1. Update all upstream versions in `gradle.properties`. The main versions you need to update are:
    - `minecraft_version`
    - `yarn_mappings_version`
    - `fabric_loader_version`
    - `fabric_api_version`

    - `supported_minecraft_versions` (if necessary)

   As well as the versions in the `dependencies` block in the `build.gradle.kts` file.
   Set `updating_minecraft` to `true` (Required for automatic data dumping).
2. Update the `NATIVE_VERSION` field in the ProtocolTranslator class to the new version
3. Update protocol constants in the `ViaFabricPlusProtocol` class
4. Update `ItemTranslator#getClientboundItemType` if a new item type exists
5. Decompile the game source code with the tool of your choice.
6. Try to compile the mod and start porting the code until all existing fixes are working again.
7. Check all data dumps and diffs in the fixes/data package and update them if necessary, here is a list of some
   critical ones:
    - `ResourcePackHeaderDiff` (add the new version at the top of the list)
    - `ItemRegistryDiff` (add all new items/blocks added in the new version)
    - `EntityDimensionDiff` (add entity dimension changes)
   For this process run `gradle test` which will automatically dump changes of diff classes into `run/`.
8. Check all mixins in the injection package if they still apply correctly, here is a list of some critical ones:
    - `MixinClientWorld#tickEntity` and `MixinClientWorld#tickPassenger`
    - `MixinPlayer#getBlockBreakingSpeed`
9. Diff the game code with the code of the previous version (e.g. using git) and implement all changes that could be
   relevant for ViaFabricPlus, those are:
    - General logic changes (e.g. `if (a && b)` -> `if (b || a)`)
    - Changes to the movement code (e.g. `player.yaw` -> `player.headYaw`)
    - Networking changes (e.g. sending a new packet / changing the packet structure)
    - Changes to visuals (e.g. animation changes)
    - Note: ViaVersion already implements most gameplay related changes for us, but you should always check for
      edge-cases. Since ViaVersion
      is primarily a server side plugin, it does not take care of client-side related / deeper changes. ViaFabricPlus
      often has to inject into
      ViaVersion code to improve funcitonality for the client.

   **Read more about which fixes should be
   added [HERE](../CONTRIBUTING.md#adding-protocol-new-fixes---which-are-important-and-which-arent)**

    - From experience, the following packages contain the usual important changes (mojang mappings):
        - `net.minecraft`
        - `net.minecraft.client`
            - `net.minecraft.client.gui`
            - `net.minecraft.client.multiplayer`
            - `net.minecraft.client.player`
        - `net.minecraft.util`
        - `net.minecraft.world`
            - `net.minecraft.world.entity`
            - `net.minecraft.world.inventory`
            - `net.minecraft.world.item`
            - `net.minecraft.world.level`
                - `net.minecraft.world.level.block`

    - While the following packages (mojang mappings) can be skipped completely (most of the time):
        - `com.mojang`
        - `net.minecraft.advancements`
        - `net.minecraft.commands`
        - `net.minecraft.data`
        - `net.minecraft.gametest`
        - `net.minecraft.realms`
        - `net.minecraft.recipebook`
        - `net.minecraft.references`
        - `net.minecraft.resources`
        - `net.minecraft.server`
        - `net.minecraft.sounds`
        - `net.minecraft.stats`
        - `net.minecraft.tags`

10. Check the ViaVersion/upstream protocol implementation for issues and report them if necessary or if these issues
    can't be fixed,
    without tons of work, implement a workaround in ViaFabricPlus.
11. Run the game and check all GUIs and other visuals for issues.
12. Clean your code and make sure it is readable and understandable, clientside fixes are sorted by their protocol
    versions, having
    newer fixes at the top of the file.
    Set `updating_minecraft` to `false`.
13. Create a pull request and wait for it to be reviewed and merged.

# Project structure

Every change made to the game is called a `feature`. Each feature has its package under both `features/` and
`injection/mixin/features/`, organizing utility and mixin classes for easier project maintenance and porting
Loading of features is done via `static` blocks and dummy `init` function called in the `FeaturesLoading` class.

## Build files

Common build logic is handled by the [BaseProject Gradle convention plugin](https://github.com/FlorianMichael/BaseProject).
Note that the root project includes the classpaths of all submodules — including optional ones like `viafabricplus-visuals`.
To avoid potential issues, ensure your code does not include any unintended references to these optional submodules.

## Release process

1. Set `project_version` in `gradle.properties` to the next release version.
2. Pin version ids of `configureVVDependencies` in `build.gradle.kts`.
3. Commit `<version> Release`.

### Versioning

The versioning should only be updated every release and should only have one update between each release.

The versioning scheme is `major.minor.patch`, where:
- `Major` versions are only incremented with breaking and fundamental changes to the existing codebase, such as
  migrating mappings or refactoring the entire codebase.

- `Minor` versions are incremented when the mod gets ported to a new version of the game or when huge features are
  added / upstream changes are implemented.

- `Patch` versions are incremented when bug fixes are made or small features are added, they are the usual version
  increment.

Usually you should go back to a -SNAPSHOT `project_version` and unpin `configureVVDependencies` again in your next commit. If the next commit
would be a merged pull request of someone else, you can do a commit with this format before merging the PR:
`Bump version to <version>`

## Git branches

See https://github.com/FlorianMichael/ViaFabricPlus-archive for older branches.

The `main` branch where all changes are merged into. After the last ViaFabricPlus release for a Minecraft version,
a branch with the version as name is created (e.g `1.21.5`).

## Backporting

Releases for older Minecraft versions are called backports (usually for updating Via* libraries). Their
version strings should be suffixed with `-BACKPORT` to identify them. Backports are pushed to the branch matching their
Minecraft version.
