package de.florianmichael.viafabricplus.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.api.version.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList;

public class ProtocolRange {
    private final ComparableProtocolVersion lowerBound;
    private final ComparableProtocolVersion upperBound;

    public ProtocolRange(ProtocolVersion lowerBound, ProtocolVersion upperBound) {
        if (lowerBound == null && upperBound == null) {
            throw new RuntimeException("Invalid protocol range");
        }
        this.lowerBound = InternalProtocolList.fromProtocolVersion(lowerBound);
        this.upperBound = InternalProtocolList.fromProtocolVersion(upperBound);
    }

    public static ProtocolRange andNewer(final ProtocolVersion version) {
        return new ProtocolRange(null, version);
    }

    public static ProtocolRange singleton(final ProtocolVersion version) {
        return new ProtocolRange(version, version);
    }

    public static ProtocolRange andOlder(final ProtocolVersion version) {
        return new ProtocolRange(version, null);
    }

    public boolean contains(final ComparableProtocolVersion protocolVersion) {
        if (this.lowerBound != null && protocolVersion.getIndex() < lowerBound.getIndex())
            return false;
        if (this.upperBound != null && protocolVersion.getIndex() > upperBound.getIndex())
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (lowerBound == null) return upperBound.getName() + "+";
        if (upperBound == null) return lowerBound.getName() + "-";
        if (lowerBound == upperBound) return lowerBound.getName();

        return lowerBound.getName() + " - " + upperBound.getName();
    }
}
