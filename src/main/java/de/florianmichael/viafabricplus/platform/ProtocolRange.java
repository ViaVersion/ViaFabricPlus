/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

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
