# Updating instructions for the project

## Update translation files
Translation files are located in `src/main/resources/assets/viafabricplus/lang/`. To update them, you need to do the following:
1. Copy the `en_us.json` file and rename it to the language code of the language you want to update (e.g. `de_de.json` for German)
2. Translate all values in the file to the language you want to update
3. Do not change the keys of the values, only the values themselves
4. Do not change the formatting of the file (e.g. the spaces between the keys and values or the order of the keys)
5. Try to be consistent with Minecraft language files.
6. Take a look at UN's guidelines for Gender-inclusive language: https://www.un.org/en/gender-inclusive-language/guidelines.shtml
7. Create a pull request and wait for it to be reviewed and merged.
8. You're done, congrats!

## Add a new feature or fix a bug
1. Create a new branch for your feature/bugfix (e.g. `feature/fix-xyz` or `fix/fix-xyz`)
2. Implement your feature/bugfix and make sure it works correctly
3. Clean your code and make sure it is readable and understandable (e.g. use proper variable names)
4. Use the Google java code style (https://google.github.io/styleguide/javaguide.html) and format your code accordingly
5. If you're changing API, make sure to update the documentation in the `docs` folder, add javadocs to your code and don't break backwards compatibility if not necessary
6. Increment the version number in `gradle.properties` by at least a patch version (e.g. 1.0.0 -> 1.0.1)
7. Create a pull request and wait for it to be reviewed and merged.
8. You're done, congrats!

## Setting up a Workspace
ViaFabricPlus uses Gradle, to make sure that it is installed properly you can check [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/ViaVersion/ViaFabricPlus`.
2. CD into the local repository.
3. Run `./gradlew genSources`.
4. Open the folder as a Gradle project in your preferred IDE.
5. Run the mod.

## Update to a new Minecraft version
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
7. Update protocol constants in the `VFPProtocol` class
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
