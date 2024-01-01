/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets.InventoryPackets;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryPackets.class, remap = false)
public abstract class MixinInventoryPackets1_17 extends ItemRewriter<ClientboundPackets1_16_2, ServerboundPackets1_17, Protocol1_17To1_16_4> {

    public MixinInventoryPackets1_17(Protocol1_17To1_16_4 protocol, Type<Item> itemType, Type<Item[]> itemArrayType) {
        super(protocol, itemType, itemArrayType);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    private void removeWindowClickHandler(CallbackInfo ci) {
        this.protocol.registerServerbound(ServerboundPackets1_17.CLICK_WINDOW, ServerboundPackets1_16_2.CLICK_WINDOW, wrapper -> {
            Via.getPlatform().getLogger().severe("Tried to remap >=1.17 CLICK_WINDOW packet which is impossible without breaking the content! Find the cause and fix it!");
            wrapper.cancel();
        }, true);
    }

}
