package de.florianmichael.viafabricplus;

import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;

public interface ViaFabricPlusAddon {

    default void onLoad() {}
    default void onChangeVersion(final ComparableProtocolVersion protocolVersion) {
    }
}
