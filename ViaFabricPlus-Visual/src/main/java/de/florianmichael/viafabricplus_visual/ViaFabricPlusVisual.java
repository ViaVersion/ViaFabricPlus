package de.florianmichael.viafabricplus_visual;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.ViaFabricPlusAddon;
import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;
import de.florianmichael.viafabricplus_visual.definition.c0_30.ClassicItemSelectionScreen;
import de.florianmichael.viafabricplus_visual.definition.v1_8_x.ArmorPointsDefinition;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class ViaFabricPlusVisual implements ViaFabricPlusAddon {

    // 1.19.2 -> 1.19
    public final static ProtocolSyncBooleanValue disableSecureChatWarning = new ProtocolSyncBooleanValue("Disable secure chat  warning", ProtocolRange.andOlder(ProtocolVersion.v1_19));

    // 1.19 -> 1.18.2
    public final static ProtocolSyncBooleanValue hideSignatureIndicator = new ProtocolSyncBooleanValue("Hide signature indicator", ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.16 -> 1.15.2
    public final static ProtocolSyncBooleanValue removeNewerFeaturesFromJigsawScreen = new ProtocolSyncBooleanValue("Remove newer features from Jigsaw screen", ProtocolRange.andOlder(ProtocolVersion.v1_15_2));

    // 1.13 -> 1.12.2
    public final static ProtocolSyncBooleanValue replacePetrifiedOakSlab = new ProtocolSyncBooleanValue("Replace petrified oak slab", new ProtocolRange(ProtocolVersion.v1_12_2, LegacyProtocolVersion.r1_3_1tor1_3_2));

    // 1.9 -> 1.8.x
    public final static ProtocolSyncBooleanValue emulateArmorHud = new ProtocolSyncBooleanValue("Emulate Armor hud", ProtocolRange.andOlder(ProtocolVersion.v1_8));
    public final static ProtocolSyncBooleanValue removeNewerFeaturesFromCommandBlockScreen = new ProtocolSyncBooleanValue("Remove newer features from Command block screen", ProtocolRange.andOlder(ProtocolVersion.v1_8));

    // a1_0_15 -> c0_28toc0_30
    public final static ProtocolSyncBooleanValue replaceCreativeInventory = new ProtocolSyncBooleanValue("Replace creative inventory", ProtocolRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));

    @Override
    public void onPostLoad() {
        ArmorPointsDefinition.load();
        ClassicItemSelectionScreen.create(InternalProtocolList.fromProtocolVersion(LegacyProtocolVersion.c0_28toc0_30));
    }

    @Override
    public void onChangeVersion(ComparableProtocolVersion protocolVersion) {
        if (ClassicItemSelectionScreen.INSTANCE != null) {
            ClassicItemSelectionScreen.INSTANCE.reload(protocolVersion);
        }
    }
}
