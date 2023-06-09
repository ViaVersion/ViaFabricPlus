/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_14to1_13_2;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import de.florianmichael.viafabricplus.definition.PacketSyncBase;
import de.florianmichael.viafabricplus.definition.v1_13_2.ScreenHandlerEmulator1_13_2;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = InventoryPackets.class, remap = false)
public class MixinInventoryPackets {

    @Inject(method = "lambda$registerPackets$0", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;warning(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void supportCustomSlots(PacketWrapper wrapper, CallbackInfo ci, Short windowId, String type, JsonElement title, Short slots, int typeId) {
        if (typeId == -1) {
            wrapper.clearPacket();
            wrapper.setPacketType(ClientboundPackets1_14.PLUGIN_MESSAGE);
            wrapper.write(Type.STRING, PacketSyncBase.PACKET_SYNC_IDENTIFIER);

            final List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(SharedConstants.getProtocolVersion(), ProtocolVersion.v1_13_2.getVersion());
            final var userConnection = ProtocolHack.createFakerUserConnection();

            try {
                var fakeOpenWindow = PacketWrapper.create(ClientboundPackets1_14.OPEN_WINDOW, Unpooled.buffer(), userConnection);
                fakeOpenWindow.write(Type.VAR_INT, windowId.intValue());
                fakeOpenWindow.write(Type.VAR_INT, typeId);
                fakeOpenWindow.write(Type.COMPONENT, title);

                fakeOpenWindow.apply(Direction.CLIENTBOUND, State.PLAY, 0, protocolPath.stream().map(ProtocolPathEntry::protocol).collect(Collectors.toList()), true);
                fakeOpenWindow.read(Type.VAR_INT);
                fakeOpenWindow.read(Type.VAR_INT);

                final String uuid = PacketSyncBase.track(ScreenHandlerEmulator1_13_2.OLD_PACKET_HANDLER);

                wrapper.write(Type.STRING, uuid);
                wrapper.write(Type.SHORT, windowId);
                wrapper.write(Type.COMPONENT, fakeOpenWindow.read(Type.COMPONENT));
                wrapper.write(Type.SHORT, slots);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ci.cancel();
        }
    }
}
