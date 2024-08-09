# Updating instructions for the project

1. Update all upstream versions in `gradle.properties`. The main versions you need to update are:
    - `minecraft_version`
    - `yarn_mappings`
    - `loader_version`
    - `fabric_api_version`
    - `viaversion_version`
    - `viabackwards_version`
    - `mod_menu_version`
2. Update the `NATIVE_VERSION` field in the ProtocolTranslator class to the new version
3. Check all mixins in the injection package if they still apply correctly, here is a list of some critical ones:
    - `MixinClientPlayerEntity#removeBl8Boolean`
    - `MixinClientWorld#tickEntity` and `MixinClientWorld#tickPassenger`
    - `MixinPlayer#getBlockBreakingSpeed`
4. Decompile the game source code with the tool of your choice.
5. Check all data dumps and diffs in the fixes/data package and update them if necessary, here is a list of some critical ones:
    - `ResourcePackHeaderDiff` (add the new version at the top of the list)
    - `ItemRegistryDiff` (add all new items/blocks added in the new version)
6. Diff the game code with the code of the previous version (e.g. using git) and implement all changes that could be relevant for ViaFabricPlus, those are:
    - General logic changes (e.g. `if (a && b)` -> `if (b || a)`)
    - Changes to the movement code (e.g. `player.yaw` -> `player.headYaw`)
    - Networking changes (e.g. sending a new packet / changing the packet structure)
    - Changes to visuals (e.g. animation changes)
    - Note: ViaVersion already implements most gameplay related changes for us, but you should always check for edge-cases. Since ViaVersion
      is primarily a server side plugin, it does not take care of client-side related / deeper changes.
   
    => If you are unsure if a change is relevant, ask in the ViaVersion discord, in general you should only implement changes
       which could be detected by a server side anti cheat.

   From experience, most changes are related to either movement or networking,
   packages like `gametest` or `server` can be skipped usually when updating. It's important to always diff code inside 
   the `net.minecraft.client` package as well as `net.minecraft.world` package, as these are the most likely to contain changes. (Mojang mappings)
7. Update protocol constants in the `ViaFabricPlusProtocol` class
8. Check the ViaVersion/upstream protocol implementation for issues and report them if necessary or if these issues can't be fixed,
   without tons of work, implement a workaround in ViaFabricPlus.
9. Run the game and check all GUIs and other visuals for issues.
10. Clean your code and make sure it is readable and understandable, clientside fixes are sorted by their protocol versions, having
   newer fixes at the top of the file.
11. Increment the version number in `gradle.properties` by at least a minor version (e.g. 1.0.0 -> 1.1.0)
12. Create a pull request and wait for it to be reviewed and merged.
13. You're done, congrats!

## Git branches
- `main`: The main branch, this is where all changes are merged into
- `backport/*`: Backport branches, these are used to backport newer ViaFabricPlus versions to older versions of the game
- `recode/*`: Recode branches, these are used to port ViaFabricPlus to newer versions of the game or rewrite big parts of the code
- `<version>`: Final release branches sorted by their Minecraft version (e.g. `1.8.9`, `1.16.5`, `1.17.1`, ...)

## Versioning
The versioning should only be updated every release and should only have one update between each release.
- The versioning scheme is `major.minor.patch`, where:
    - `major` is incremented when breaking changes are made
    - `minor` is incremented when new features are added
    - `patch` is incremented when bug fixes are made

This scheme is used as follows:
- `Major` versions are only incremented with breaking and fundamental changes to the existing codebase, such as migrating mappings
or refactoring the entire codebase.

- `Minor` versions are incremented when the mod gets ported to a new version of the game or when huge features are added / 
upstream changes are implemented.

- `Patch` versions are incremented when bug fixes are made or small features are added, they are the usual version increment.