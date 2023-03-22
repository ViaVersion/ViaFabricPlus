# Developer API
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
| Callback class name                  | Description                                                                                                                                                                                                   |
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ChangeProtocolVersionCallback        | Called when the user changes the target version in the screen, or if you connect to a server for which a specific version has been selected, you disconnect, the event for the actual version is also called. |
| FinishMinecraftLoadCallback          | Called when Minecraft is finished with loading all its components                                                                                                                                             |
| FinishViaLoadingBaseStartupCallback  | Called when ViaLoadingBase and Via* is loaded and ready to use                                                                                                                                                |
| InitializeSettingsCallback           | Called after the default setting groups are loaded and before the setting config is loaded                                                                                                                    |
| LoadClassicProtocolExtensionCallback | Called when the classic server sends the protocol extensions (only in **c0.30 CPE**)                                                                                                                          |
| PreLoadCallback                      | Called before everything (Pre-pre load)                                                                                                                                                                       |
| SkipIdlePacketCallback               | Called as soon as the idle packet is skipped in the <= 1.8                                                                                                                                                    |

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
    }
}
```
and then you register the setting group in your onLoad method:
```java
PreLoadCallback.EVENT.register(() -> {
    ViaFabricPlus.INSTANCE.getSettingsSystem().addGroup(ExampleSettingGroup.INSTANCE);
});
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