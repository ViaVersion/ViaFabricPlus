/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import de.florianmichael.viafabricplus.fixes.ChestHandler1_13_2;
import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import de.florianmichael.viafabricplus.protocolhack.translator.TextComponentTranslator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = InventoryPackets.class, remap = false)
public abstract class MixinInventoryPackets {

    @Inject(method = "lambda$registerPackets$0", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;warning(Ljava/lang/String;)V", remap = false), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void supportLargeContainers(PacketWrapper wrapper, CallbackInfo ci, Short windowId, String type, JsonElement title, Short slots) {
        if ((type.equals("minecraft:container") || type.equals("minecraft:chest")) && (slots > 54 || slots <= 0)) {
            ci.cancel();

            final String uuid = ClientsideFixes.executeSyncTask(ChestHandler1_13_2.OLD_PACKET_HANDLER);
            wrapper.clearPacket();
            wrapper.setPacketType(ClientboundPackets1_14.PLUGIN_MESSAGE);
            wrapper.write(Type.STRING, ClientsideFixes.PACKET_SYNC_IDENTIFIER);
            wrapper.write(Type.STRING, uuid);
            wrapper.write(Type.UNSIGNED_BYTE, windowId);
            wrapper.write(Type.UNSIGNED_BYTE, slots);
            wrapper.write(Type.COMPONENT, TextComponentTranslator.via1_14toViaLatest(title));
        }
    }

}
