package de.florianmichael.viafabricplus_visual;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;
import de.florianmichael.viafabricplus_visual.definition.ArmorPointsDefinition;
import net.fabricmc.api.ClientModInitializer;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class ViaFabricPlusVisual implements ClientModInitializer {

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

    @Override
    public void onInitializeClient() {
        ArmorPointsDefinition.load();
    }
}
