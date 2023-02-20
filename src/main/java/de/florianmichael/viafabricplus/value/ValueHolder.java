package de.florianmichael.viafabricplus.value;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.impl.BooleanValue;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;

import java.util.ArrayList;
import java.util.List;

public class ValueHolder {
    private final static List<AbstractValue<?>> values = new ArrayList<>();

    // General settings
    public static final BooleanValue removeNotAvailableItemsFromCreativeTab = new BooleanValue("Remove not available items from creative tab", true);

    // 1.14 -> 1.13.2
    public static final ProtocolSyncBooleanValue smoothOutMerchantScreens = new ProtocolSyncBooleanValue("Smooth out merchant screens", ProtocolRange.andOlder(ProtocolVersion.v1_13_2));

    // 1.13 -> 1.12.2
    public static final ProtocolSyncBooleanValue executeInputsInSync = new ProtocolSyncBooleanValue("Execute inputs in sync", ProtocolRange.andOlder(ProtocolVersion.v1_12_2));

    // 1.9 -> 1.8.x
    public static final ProtocolSyncBooleanValue removeCooldowns = new ProtocolSyncBooleanValue("Remove cooldowns", ProtocolRange.andOlder(ProtocolVersion.v1_8));

    public static void setup() {
    }
}
