# ViaFabricPlus
Clientside ViaVersion, ViaLegacy, ViaBedrock and ViaAprilFools implementation with clientside fixes for Fabric
### This project has nothing to do with the original ViaFabric and is therefore also not compact

## Why?
ViaFabricPlus is supposed to be an alternative to [multiconnect](https://github.com/Earthcomputer/multiconnect) that offers more compactness and more clientside improvements,
as ViaFabricPlus implements all Via platforms (ViaVersion, ViaBackwards, ViaLegacy, ViaAprilFools, ViaBedrock) and adds tons of clientside fixes and QoL improvements like old rendering for all platforms. 

## Contact
If you encounter any issues, please report them on the
[issue tracker](https://github.com/FlorianMichael/ViaFabricPlus/issues).  
If you just want to talk or need help with ViaFabricPlus feel free to join my
[Discord](https://discord.gg/BwWhCHUKDf).

## Compatibility
ViaFabricPlus is structured to interfere with mods as little as possible.
It should work fine with most if not all mods and modpacks.

### Known incompatibilities:

- ***[ViaFabric](https://github.com/ViaVersion/ViaFabric)***
- ***[multiconnect](https://github.com/Earthcomputer/multiconnect)***
- ***[Krypton](https://github.com/astei/krypton)***

## Basic Features
- [x] ViaVersion implementation
- [x] ViaBackwards implementation
- [x] ViaLegacy implementation
- [x] ViaAprilFools implementation
- [x] (Beta) ViaBedrock implementation
- [x] BetaCraft implementation for MP Pass

## Clientside related Fixes
- [x] Bounding boxes for all versions
- [x] Entity interaction and movement related packet fixes
- [x] Sync and async mouse/keyboard handling in <= 1.12.2
- [x] Mining speeds and item attributes
- [x] Filter item creative tabs for only available items
- [x] Combat system in <= 1.8
- [x] Tons of modifications to ViaVersion to make it more legit (Metadata fixes, broken packets, edge-cases)
- [x] Visual and screen related changes (newer Command Block features, GameMode selection, ...)
- [x] ViaAprilFools and ViaLegacy extensions to make it more legit
- [x] Chat signatures (secure login) for all versions (1.19.0, 1.19.1, 1.19.2)
- [x] Address parsing for all minecraft versions
- [x] PackFormats and HTTP Header for all resource pack versions
- [x] Raytrace related fixes in <= 1.8
- [x] Implementing HUD changes for <= b1.7.3
- [x] Chat lengths of all versions
- [x] Implementing non-sequenced block placement in <= 1.18.2
- [x] Animation related fixes (1.7 Sneaking, c0.30 walking animation, ...)
- [x] Fixed clientside packet handling (1.16.5 transactions, 1.19.0 tablist, ...)

## TODO
- [ ] Bedrock account auth for ViaBedrock
- [ ] ClassiCube implementation for MP Pass
- [ ] BetaCraft server list screen
- [ ] More extensions for Classic Protocol Extensions protocol
- [ ] Window click interactions in <= 1.16.5

## Dependencies
| Dependency     | Download                                                   |
|----------------|------------------------------------------------------------|
| Fabric API     | https://github.com/fabricMC/fabric                         |
| ViaVersion     | https://github.com/ViaVersion/ViaVersion                   |
| ViaBackwards   | https://github.com/ViaVersion/ViaBackwards                 |
| Snake YAML     | https://mvnrepository.com/artifact/org.yaml/snakeyaml/1.33 |
| ViaLegacy      | https://github.com/RaphiMC/ViaLegacy                       |
| ViaAprilFools  | https://github.com/RaphiMC/ViaAprilFools                   |
| ViaBedrock     | https://github.com/RaphiMC/ViaBedrock                      |
| MC-Structs     | https://github.com/Lenni0451/MCStructs                     |
| Reflect        | https://github.com/Lenni0451/Reflect                       |
| ViaLoadingBase | https://github.com/FlorianMichael/ViaLoadingBase           |

## Setting up a Workspace
ViaFabricPlus uses Gradle, to make sure that it is installed properly you can check [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/FlorianMichael/ViaFabricPlus`.
2. CD into the local repository.
3. Run `./gradlew genSources`.
4. Open the folder as a Gradle project in your preferred IDE.
5. Run the mod.

## Settings
Settings are optional settings that can turn fixes on and off, originally they were used for debugging<br>
![](/.github/images/settings.png)

# Classic stuff
## Custom protocol extensions
ViaFabricPlus implements new Classic Extensions into the CPE protocol of ViaLegacy which are rather client side. <br>
- **WeatherType** extension (version **1**)

## Protocol commands
To better control the Classic Protocol, there are a few clientside commands, the command prefix is **/v**: <br>
- **/vhelp** - Displays all commands, available from: **c0.28-c0.30**
- **/vsettime <Time (Long)>** - Changes the Clientside World Time, available from: **c0.28-c0.30**
- **/vlistextensions** - Displays all classic protocol extensions, available in: **c0.30 CPE**

## Addons
There is no real addon base, to create addons you can simply use the event system, which uses Fabric's Event-API.
```java
public class ViaFabricPlusExampleAddon implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ChangeProtocolVersionCallback.EVENT.register(protocolVersion -> {
      System.out.println("Version changed to " + protocolVersion.getName());
    });
  }
}
```
#### ViaFabricPlus has 7 events at the moment:
| Callback class name                  | Description                                                                                |
|--------------------------------------|--------------------------------------------------------------------------------------------|
| ChangeProtocolVersionCallback        | Called when the user changes the target version in the screen                              |
| FinishMinecraftLoadCallback          | Called when Minecraft is finished with loading all its components                          |
| FinishViaLoadingBaseStartupCallback  | Called when ViaLoadingBase and Via* is loaded and ready to use                             |
| InitializeSettingsCallback           | Called after the default setting groups are loaded and before the setting config is loaded |
| LoadClassicProtocolExtensionCallback | Called when the classic server sends the protocol extensions (only in **c0.30 CPE**)       |
| PreLoadCallback                      | Called before everything (Pre-pre load)                                                    |
| SkipIdlePacketCallback               | Called as soon as the idle packet is skipped in the <= 1.8                                 |

### General API
#### Get the release version of an material:
```java
final ProtocolRange range = ItemReleaseVersionDefinition.INSTANCE.getItemMap().get(Items.WRITABLE_BOOK); // If an item does not appear in the item map, it has always existed

// The Range class then contains all versions in which the item occurs. 
// You can find out how the Range class works in the ViaLoadingBase README.
// https://github.com/FlorianMichael/ViaLoadingBase
```

#### Creating own settings for the settings screen:
```java
public class ExampleSettingGroup extends SettingGroup {
    public final static ExampleSettingGroup INSTANCE = new ExampleSettingGroup();
    
    public final BooleanSetting test = new BooleanSetting("Test", false);
    
    public ExampleSettingGroup() {
        super("Example");
        ViaFabricPlus.INSTANCE.getSettingsSystem().addGroup(this); // should be in your onLoad method
    }
}
```

#### Implementing classic protocol commands:
```java
public class ExampleCommand implements ICommand {

    @Override
    public String name() {
        return "example";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void execute(String[] args) {
    }
}
```
and then you register the command in your onLoad method:
```java
PreLoadCallback.EVENT.register(() -> {
    ClassicProtocolCommands.commands.add(new ExampleCommand());
});
```

#### Implementing custom classic protocol extensions:
```java
public class ExampleExtensionSupport implements ClientModInitializer {

  public static ClientboundPacketsc0_30cpe EXT_CLICK_DISTANCE;

  @Override
  public void onInitializeClient() {
    PreLoadCallback.EVENT.register(() -> {
      CustomClassicProtocolExtensions.allowExtension(ClassicProtocolExtension.CLICK_DISTANCE); // Register extension as supported

      EXT_CLICK_DISTANCE = CustomClassicProtocolExtensions.createNewPacket(ClassicProtocolExtension.CLICK_DISTANCE, 0x12, (user, buf) -> buf.readShort());
    });

    FinishViaLoadingBaseStartupCallback.EVENT.register(() -> {
      Via.getManager().getProtocolManager().getProtocol(Protocolc0_30toc0_30cpe.class).registerClientbound(EXT_CLICK_DISTANCE, null, new PacketHandlers() {
        @Override
        protected void register() {
          handler(wrapper -> {
            wrapper.cancel();
            final short distance = wrapper.read(Type.SHORT);
            // Do your stuff...
          });
        }
      }, true);
    });
  }
}
```

## Alternatives
- [ClientViaVersion](https://github.com/Gerrygames/ClientViaVersion): Discontinued 5zig plugin.
- [multiconnect](https://www.curseforge.com/minecraft/mc-mods/multiconnect): Fabric mod for connecting to older
  versions: down to 1.11 (stable) and 1.8 (experimental).
- [ViaFabric](https://www.curseforge.com/minecraft/mc-mods/viafabric): Client-side and server-side ViaVersion implementation for Fabric

## WARNING
**I cannot guarantee that this mod is allowed on every (or even any) server. This mod may cause problems with anti cheat
plugins. USE AT OWN RISK**
