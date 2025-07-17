<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
  <img src="https://raw.githubusercontent.com/ViaVersion/ViaFabricPlus/main/src/main/resources/assets/viafabricplus/icon.png" width="150" alt="ViaFabricPlus logo">
  <h1>ViaFabricPlus</h1>
  <a href="https://fabricmc.net"><img src="https://img.shields.io/badge/Mod%20Loader-Fabric-lightyellow?logo=fabric" alt="Mod Loader: Fabric"></a>
  <img src="https://img.shields.io/badge/Environment-Client-purple" alt="Environment: Client">
  <a href="https://discord.gg/viaversion"><img src="https://img.shields.io/discord/316206679014244363?color=0098DB&label=Discord&logo=discord&logoColor=0098DB" alt="Discord"></a><br/>
  <a href="https://modrinth.com/mod/viafabricplus"><img src="https://img.shields.io/badge/dynamic/json?color=158000&label=downloads&prefix=+%20&query=downloads&url=https://api.modrinth.com/v2/project/rIC2XJV4&logo=modrinth" alt="Modrinth Downloads"></a>
  <a href="https://curseforge.com/minecraft/mc-mods/viafabricplus"><img src="https://cf.way2muchnoise.eu/full_830604_downloads.svg" alt="CurseForge Downloads"></a>
  <a href="https://github.com/ViaVersion/ViaFabricPlus/actions/workflows/build.yml"><img src="https://github.com/ViaVersion/ViaFabricPlus/actions/workflows/build.yml/badge.svg?branch=main" alt="Build Status"></a>

  <p><strong>Minecraft Fabric mod which allows you to join <em>every</em> Minecraft server version (Classic, Alpha, Beta, Release, April Fools, Bedrock)</strong></p>
</div>

# Why another protocol translator?

ViaFabricPlus implements the [ViaVersion projects](https://github.com/ViaVersion) into Fabric and provides tons of fixes
to the existing protocol translation which can't be implemented in the original ViaVersion project.
These fixes consist of movement changes, block/entity collisions, rendering changes, and many more.

At the time of writing, ViaFabricPlus is the only mod that supports joining all Minecraft server versions down to the
first multiplayer version while implementing
legacy combat mechanics, movement, and rendering changes to make the gameplay feel more like the old days.

**On the other hand, ViaFabricPlus supports only the latest Minecraft client version, and only Fabric.**
If you need ViaFabricPlus for older versions of the game, you can use [ViaFabric](https://viaversion.com/fabric)

## Supported Server versions

- Release (1.0.0 - 1.21.8)
- Beta (b1.0 - b1.8.1)
- Alpha (a1.0.15 - a1.2.6)
- Classic (c0.0.15 - c0.30 including [CPE](https://wiki.vg/Classic_Protocol_Extension))
- April Fools (3D Shareware, 20w14infinite, 25w14craftmine)
- Combat Snapshots (Combat Test 8c)
- Bedrock Edition 1.21.93 ([Some features are missing](https://github.com/RaphiMC/ViaBedrock#features))

## How to (Users)

- [A detailed guide on how to install and use the mod](docs/USAGE.md)
- If you encounter any issues, please report them on either:
    - [the issue tracker](https://github.com/ViaVersion/ViaFabricPlus/issues)
    - [the ViaVersion Discord](https://discord.gg/viaversion)

## How to (Developers)

- [Detailed guidelines for contributions as well as setting up a dev environment](CONTRIBUTING.md)
- [API and integration examples for developers](docs/DEVELOPER_API.md)

## ViaFabric

[ViaFabric](https://github.com/ViaVersion/ViaFabric) can be used for server-side purposes or when using older versions
of the game.

### Does it work with ViaFabricPlus:

- No, ViaFabricPlus cannot be used with ViaFabric.

### Differences with ViaFabric:

|                                  | ViaFabric                                       | ViaFabricPlus                                                   |
|----------------------------------|-------------------------------------------------|-----------------------------------------------------------------|
| Can be installed on              | Multiple client/server versions with fabric     | Latest client-side version with fabric                          |
| Objectives                       | Simply implement ViaVersion                     | Implements ViaVersion with client-side fixes to version changes |
| How does it work?                | Modifying packets at network code               | Modifying client code more deeply                               |
| Triggering anti-cheats           | Very likely                                     | Mostly not                                                      |

## Credits

Special thanks to all our [Contributors](https://github.com/ViaVersion/ViaFabricPlus/graphs/contributors).

## Disclaimer

It cannot be guaranteed that this mod is allowed on specific servers as it can possibly cause problems with anti-cheat
plugins.\
***(USE ONLY WITH CAUTION!)***
