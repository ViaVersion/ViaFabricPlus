package de.florianmichael.viafabricplus;

import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;

public interface ViaFabricPlusAddon {

    default void onPreLoad() {
    }
    default void onPostLoad() {
    }

    default void onChangeVersion(final ComparableProtocolVersion protocolVersion) {
    }
}
