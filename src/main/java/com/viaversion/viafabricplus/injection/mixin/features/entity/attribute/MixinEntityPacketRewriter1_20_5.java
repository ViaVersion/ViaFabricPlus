/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.injection.mixin.features.entity.attribute;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.rewriter.EntityPacketRewriter1_20_5;
import java.util.UUID;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityPacketRewriter1_20_5.class, remap = false)
public abstract class MixinEntityPacketRewriter1_20_5 {

    @Shadow
    protected abstract void writeAttribute(PacketWrapper wrapper, String attributeId, double base, UUID modifierId, double amount);

    @Redirect(method = "sendRangeAttributes", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/v1_20_3to1_20_5/rewriter/EntityPacketRewriter1_20_5;writeAttribute(Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;Ljava/lang/String;DLjava/util/UUID;D)V"))
    private void useLegacyValues(EntityPacketRewriter1_20_5 instance, PacketWrapper wrapper, String attributeId, double base, UUID modifierId, double amount) {
        if (attributeId.equals("player.block_interaction_range") && wrapper.user().getProtocolInfo().serverProtocolVersion().olderThan(LegacyProtocolVersion.r1_0_0tor1_0_1)) {
            this.writeAttribute(wrapper, attributeId, 4D, modifierId, 1D);
        } else if (attributeId.equals("player.entity_interaction_range") && wrapper.user().getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            this.writeAttribute(wrapper, attributeId, 3D, modifierId, 3D);
        } else {
            this.writeAttribute(wrapper, attributeId, base, modifierId, amount);
        }
    }

}
