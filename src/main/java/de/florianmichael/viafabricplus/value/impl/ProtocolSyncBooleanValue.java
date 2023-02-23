package de.florianmichael.viafabricplus.value.impl;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.AbstractValue;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public class ProtocolSyncBooleanValue extends AbstractValue<Boolean> {
    private final ProtocolRange protocolRange;

    private boolean syncWithProtocol;

    public ProtocolSyncBooleanValue(String name, ProtocolRange protocolRange) {
        super(name + " (" + protocolRange.toString() + ")", true);

        this.protocolRange = protocolRange;
        this.syncWithProtocol = true;
    }

    @Override
    public void write(JsonObject object) {
        final JsonObject valueNode = new JsonObject();

        valueNode.addProperty("value", this.getValue());
        valueNode.addProperty("sync-with-protocol", this.isSyncWithProtocol());

        object.add(getName(), valueNode);
    }

    @Override
    public void read(JsonObject object) {
        if (!object.has(getName())) return;
        final JsonObject valueNode = object.get(getName()).getAsJsonObject();

        setValue(valueNode.get("value").getAsBoolean());
        setSyncWithProtocol(valueNode.get("sync-with-protocol").getAsBoolean());
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
