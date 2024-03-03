/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityIdRewriter.class, remap = false)
public abstract class MixinEntityIdRewriter {

    @Inject(method = "toClientItem(Lcom/viaversion/viaversion/api/minecraft/item/Item;Z)V", at = @At("HEAD"))
    private static void handleNegativeItemCountS2C(Item item, boolean backwards, CallbackInfo ci) {
        if (item != null && item.amount() <= 0) {
            CompoundTag tag = item.tag();
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
            }

            tag.put(ClientsideFixes.ITEM_COUNT_NBT_TAG, new ByteTag((byte) item.amount()));
            item.setTag(tag);
        }
    }

    @Inject(method = "toServerItem(Lcom/viaversion/viaversion/api/minecraft/item/Item;Z)V", at = @At("HEAD"))
    private static void handleNegativeItemCountC2S(Item item, boolean backwards, CallbackInfo ci) {
        if (item != null && item.tag() != null) {
            if (item.tag().contains(ClientsideFixes.ITEM_COUNT_NBT_TAG)) {
                item.setAmount(item.tag().<ByteTag>remove(ClientsideFixes.ITEM_COUNT_NBT_TAG).asByte());
                if (item.tag().isEmpty()) item.setTag(null);
            }
        }
    }

}
