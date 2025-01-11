# Contributing guidelines for the project

## Setting up a Workspace

ViaFabricPlus uses Gradle, to make sure that it is installed properly you can
check [Gradle's website](https://gradle.org/install/).

1. Clone the repository using `git clone https://github.com/ViaVersion/ViaFabricPlus`.
2. CD into the local repository.
3. Run `./gradlew genSources`.
4. Open the folder as a Gradle project in your preferred IDE.
5. Run the mod.

## Update translation files

Translation files are located in `src/main/resources/assets/viafabricplus/lang/`. To update them, you need to do the
following:

1. Copy the `en_us.json` file and rename it to the language code of the language you want to update (e.g. `de_de.json`
   for German)
2. Translate all values in the file to the language you want to update
3. Do not change the keys of the values, only the values themselves
4. Do not change the formatting of the file (e.g. the spaces between the keys and values or the order of the keys)
5. Try to be consistent with Minecraft language files.
6. Take a look at UN's guidelines for Gender-inclusive
   language: https://www.un.org/en/gender-inclusive-language/guidelines.shtml
7. Create a pull request and wait for it to be reviewed and merged.
8. You're done, congrats!

## Add a new feature or fix a bug

1. Create a new branch for your feature/bugfix (e.g. `feature/fix-xyz` or `fix/fix-xyz`)
2. Implement your feature/bugfix and make sure it works correctly
3. Clean your code and make sure it is readable and understandable (e.g. use proper variable names)
4. Use the Google java code style (https://google.github.io/styleguide/javaguide.html) and format your code accordingly
5. If you're changing API, make sure to update the documentation in the `docs` folder, add javadocs to your code and
   don't break backwards compatibility if not necessary
6. Create a pull request and wait for it to be reviewed and merged.
7. You're done, congrats!

## Adding protocol new fixes - which are important and which aren't?

Protocol fixes are the core functionality of ViaFabricPlus, they make ViaFabricPlus stand out from all other existing
protocol translation platforms, and so it's even more important to keep them working.

When adding new fixes, you should always check if the fix is relevant for the current version of the game. If you're
unsure,
ask in the ViaVersion discord.

In general, you should only implement changes which could be detected by the server or are relevant for the gameplay.

From experience, most changes are related to either movement or networking. If you contribute new fixes, please proof
your changes by showing that they are relevant and also by providing a game source diff. **It's important that fixes
are changing the actual change happend in the game and not just visual changes.**

## Maintaining the mod

See [MAINTAINING.md](docs/MAINTAINING.md) for more information on how to maintain the mod.