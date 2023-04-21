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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_16to1_15_2;

import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import de.florianmichael.viafabricplus.injection.access.IInventoryTracker1_16;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = InventoryPackets.class, remap = false)
public class MixinInventoryPackets extends ItemRewriter<ClientboundPackets1_15, ServerboundPackets1_16, Protocol1_16To1_15_2> {

    protected MixinInventoryPackets(Protocol1_16To1_15_2 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void fixInventoryIssue(CallbackInfo ci, PacketHandler cursorRemapper) {
        protocol.registerClientbound(ClientboundPackets1_15.CLOSE_WINDOW, ClientboundPackets1_16.CLOSE_WINDOW, new PacketHandlers() {
            @Override
            public void register() {
                handler(cursorRemapper);
                handler(wrapper -> {
                    InventoryTracker1_16 inventoryTracker = wrapper.user().get(InventoryTracker1_16.class);
                    ((IInventoryTracker1_16) inventoryTracker).viafabricplus_setInventoryOpen(false);
                });
            }
        }, true);
        protocol.registerServerbound(ServerboundPackets1_16.CLOSE_WINDOW, ServerboundPackets1_14.CLOSE_WINDOW, wrapper -> {
            InventoryTracker1_16 inventoryTracker = wrapper.user().get(InventoryTracker1_16.class);
            ((IInventoryTracker1_16) inventoryTracker).viafabricplus_setInventoryOpen(false);
        }, true);
    }
}
