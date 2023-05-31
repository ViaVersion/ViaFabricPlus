# Developer API
There is no real addon base, to create addons you can simply use the event system, which uses Fabric's Event-API.
```java
public class ViaFabricPlusExampleAddon implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ChangeProtocolVersionCallback.EVENT.register(versionEnum -> {
      System.out.println("Version changed to " + versionEnum.getName());
    });
  }
}
```
#### ViaFabricPlus has 6 events at the moment:
| Callback class name                  | Description                                                                                                                                                                                                   |
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ChangeProtocolVersionCallback        | Called when the user changes the target version in the screen, or if you connect to a server for which a specific version has been selected, you disconnect, the event for the actual version is also called. |
| FinishMinecraftLoadCallback          | Called when Minecraft is finished with loading all its components                                                                                                                                             |
| FinishViaVersionStartupCallback      | Called when ViaVersion is loaded and ready to use                                                                                                                                                             |
| InitializeSettingsCallback           | Called after the default setting groups are loaded and before the setting config is loaded                                                                                                                    |
| LoadClassicProtocolExtensionCallback | Called when the classic server sends the protocol extensions (only in **c0.30 CPE**)                                                                                                                          |
| PreLoadCallback                      | Called before everything (Pre-pre load)                                                                                                                                                                       |

### General API
#### Get the release version of a material:
```java
final VersionRange range = ItemReleaseVersionDefinition.INSTANCE.getItemMap().get(Items.WRITABLE_BOOK); // If an item does not appear in the item map, it has always existed

// The Range class then contains all versions in which the item occurs. 
// https://github.com/ViaVersion/ViaLoader
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