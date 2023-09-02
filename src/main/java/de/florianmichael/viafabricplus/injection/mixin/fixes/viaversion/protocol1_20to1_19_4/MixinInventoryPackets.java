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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_20to1_19_4;

import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ServerboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_20to1_19_4.Protocol1_20To1_19_4;
import com.viaversion.viaversion.protocols.protocol1_20to1_19_4.packets.InventoryPackets;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import de.florianmichael.viafabricplus.definition.ClientsideFixes;
import de.florianmichael.viafabricplus.definition.screen.CustomScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryPackets.class, remap = false)
public class MixinInventoryPackets extends ItemRewriter<ClientboundPackets1_19_4, ServerboundPackets1_19_4, Protocol1_20To1_19_4> {

    public MixinInventoryPackets(Protocol1_20To1_19_4 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void handleLegacySmithingScreen(CallbackInfo ci) {
        protocol.registerClientbound(ClientboundPackets1_19_4.OPEN_WINDOW, ClientboundPackets1_19_4.OPEN_WINDOW, new PacketHandlers() {
            @Override
            protected void register() {
                map(Type.VAR_INT); // Window ID
                map(Type.VAR_INT); // Type ID
                map(Type.COMPONENT); // Title

                handler(wrapper -> {
                    final var windowId = wrapper.get(Type.VAR_INT, 0);
                    final var typeId = wrapper.get(Type.VAR_INT, 1);
                    final var title = wrapper.get(Type.COMPONENT, 0);

                    if (typeId == 20) {
                        wrapper.clearPacket();
                        wrapper.setPacketType(ClientboundPackets1_19_4.PLUGIN_MESSAGE);

                        wrapper.write(Type.STRING, ClientsideFixes.PACKET_SYNC_IDENTIFIER);
                        wrapper.write(Type.STRING, ClientsideFixes.executeSyncTask(CustomScreenHandler.LEGACY_SMITHING_HANDLER));
                        wrapper.write(Type.VAR_INT, windowId);
                        wrapper.write(Type.COMPONENT, title);
                    } else {
                        final var mappedId = protocol.getMappingData().getMenuMappings().getNewId(typeId);
                        if (mappedId == -1) {
                            wrapper.cancel();
                        } else {
                            wrapper.set(Type.VAR_INT, 1, mappedId);
                        }
                    }
                });
            }
        }, true);
    }
}
