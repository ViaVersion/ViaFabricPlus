package de.florianmichael.viafabricplus.value.impl;

import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.AbstractValue;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public class ProtocolSyncBooleanValue extends AbstractValue<Boolean> {
    private final ProtocolRange protocolRange;

    private boolean syncWithProtocol;

    public ProtocolSyncBooleanValue(String name, ProtocolRange protocolRange) {
        super(name, true);

        this.protocolRange = protocolRange;
    }

    @Override
    public Boolean getValue() {
        if (this.syncWithProtocol) return protocolRange.contains(ViaLoadingBase.getTargetVersion());
        return super.getValue();
    }

    public boolean isSyncWithProtocol() {
        return syncWithProtocol;
    }

    public void setSyncWithProtocol(boolean syncWithProtocol) {
        this.syncWithProtocol = syncWithProtocol;
    }
}
