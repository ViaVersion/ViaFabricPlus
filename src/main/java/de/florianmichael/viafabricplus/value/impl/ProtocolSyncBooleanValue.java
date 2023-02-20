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

package de.florianmichael.viafabricplus.value.impl;

import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.AbstractValue;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public class ProtocolSyncBooleanValue extends AbstractValue<Boolean> {
    private final ProtocolRange protocolRange;

    private boolean syncWithProtocol;

    public ProtocolSyncBooleanValue(String name, ProtocolRange protocolRange) {
        super(name + " (" + protocolRange.toString() + ")", true);

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
