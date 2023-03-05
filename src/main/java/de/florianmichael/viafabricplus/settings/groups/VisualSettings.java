package de.florianmichael.viafabricplus.settings.groups;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.impl.ProtocolSyncBooleanSetting;
import de.florianmichael.vialoadingbase.platform.ProtocolRange;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class VisualSettings extends SettingGroup {
    public final static VisualSettings self = new VisualSettings();

    // 1.19.2 -> 1.19
    public final ProtocolSyncBooleanSetting disableSecureChatWarning = new ProtocolSyncBooleanSetting(this, "Disable secure chat  warning", ProtocolRange.andOlder(ProtocolVersion.v1_19));

    // 1.19 -> 1.18.2
    public final ProtocolSyncBooleanSetting hideSignatureIndicator = new ProtocolSyncBooleanSetting(this, "Hide signature indicator", ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.16 -> 1.15.2
    public final ProtocolSyncBooleanSetting removeNewerFeaturesFromJigsawScreen = new ProtocolSyncBooleanSetting(this, "Remove newer features from Jigsaw screen", ProtocolRange.andOlder(ProtocolVersion.v1_15_2));

    // 1.13 -> 1.12.2
    public final ProtocolSyncBooleanSetting replacePetrifiedOakSlab = new ProtocolSyncBooleanSetting(this, "Replace petrified oak slab", new ProtocolRange(ProtocolVersion.v1_12_2, LegacyProtocolVersion.r1_3_1tor1_3_2));

    // 1.9 -> 1.8.x
    public final ProtocolSyncBooleanSetting emulateArmorHud = new ProtocolSyncBooleanSetting(this, "Emulate Armor hud", ProtocolRange.andOlder(ProtocolVersion.v1_8));
    public final ProtocolSyncBooleanSetting removeNewerFeaturesFromCommandBlockScreen = new ProtocolSyncBooleanSetting(this, "Remove newer features from Command block screen", ProtocolRange.andOlder(ProtocolVersion.v1_8));

    // r1_0_0tor1_0_1 -> b1_8tob1_8_1
    public final ProtocolSyncBooleanSetting replaceHurtSoundWithOOFSound = new ProtocolSyncBooleanSetting(this, "Replace hurt sound with OOF sound", ProtocolRange.andOlder(LegacyProtocolVersion.b1_8tob1_8_1));

    // a1_0_15 -> c0_28toc0_30
    public final ProtocolSyncBooleanSetting replaceCreativeInventory = new ProtocolSyncBooleanSetting(this, "Replace creative inventory", ProtocolRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));
    public final ProtocolSyncBooleanSetting oldWalkingAnimation = new ProtocolSyncBooleanSetting(this, "Old walking animation", ProtocolRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));

    public VisualSettings() {
        super("Visual");
    }

    public static VisualSettings getClassWrapper() {
        return VisualSettings.self;
    }
}
