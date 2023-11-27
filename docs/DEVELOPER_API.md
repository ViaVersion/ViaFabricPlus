# Developer API
ViaFabricPlus provides various events and APIs for developers to use. This page explains how to use them.

## Events
ViaFabricPlus events are using the [Fabric Event API](https://fabricmc.net/wiki/tutorial:events). You can register to them like this:
```java
ChangeProtocolVersionCallback.EVENT.register(versionEnum -> {
    System.out.println("Version changed to " + versionEnum.getName());
});
```
### ViaFabricPlus has 7 events at the moment
| Callback class name                  | Description                                                                                                                                                                                                   |
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ChangeProtocolVersionCallback        | Called when the user changes the target version in the screen, or if you connect to a server for which a specific version has been selected, you disconnect, the event for the actual version is also called. |
| DisconnectCallback                   | Called when the user disconnects from a server.                                                                                                                                                               |
| PostGameLoadCallback                 | Called when Minecraft is finished with loading all its components                                                                                                                                             |
| PostViaVersionLoadCallback           | Called when ViaVersion is loaded and ready to use                                                                                                                                                             |
| RegisterSettingsCallback             | Called after the default setting groups are loaded and before the setting config is loaded                                                                                                                    |
| LoadClassicProtocolExtensionCallback | Called when the classic server sends the protocol extensions (only in **c0.30 CPE**)                                                                                                                          |
| LoadCallback                         | Called at the earliest point ViaFabricPlus is injecting too                                                                                                                                                   |

## ViaVersion internals
### Add CustomPayload channels for versions below 1.13
In order to receive custom payloads with custom channels in versions below 1.13, you need to register them, that's what you do:
```java
Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().put("FML|HS", "fml:hs");
```

### Check if an item exists in a specific version
```java
final VersionRange range = ItemRegistryDiff.ITEM_DIFF.get(Items.WRITABLE_BOOK); // If an item does not appear in the item map, it has always existed

// The Range class then contains all versions in which the item occurs. 
// https://github.com/ViaVersion/ViaLoader
if (ItemRegistryDiff.contains(Items.STONE, VersionRange.andOlder(VersionEnum.r1_8))) {
    // Do something
}
```

### Creating own settings for the settings screen
```java
public class ExampleSettingGroup extends SettingGroup {
    private static final ExampleSettingGroup instance = new ExampleSettingGroup();
    
    public final BooleanSetting test = new BooleanSetting(this, Text.of("Test"), false);
    
    public ExampleSettingGroup() {
        super("Example");
    }
    
    public static ExampleSettingGroup global() {
        return instance;
    }
}
```

and then you register the setting group in your onLoad method
```java
RegisterSettingsCallback.EVENT.register(state -> {
    if (state == RegisterSettingsCallback.State.POST) {
        ViaFabricPlus.global().getSettingsManager().addGroup(ExampleSettingGroup.INSTANCE);
    }
});
```
