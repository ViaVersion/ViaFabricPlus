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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.network;

import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import de.florianmichael.viafabricplus.injection.access.IItemStack;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PacketByteBuf.class)
public abstract class MixinPacketByteBuf {

    @Redirect(method = "readItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    private void removeViaFabricPlusTag(ItemStack instance, NbtCompound tag) {
        if (tag != null && tag.contains(ClientsideFixes.ITEM_COUNT_NBT_TAG, NbtElement.BYTE_TYPE) && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_10)) {
            final IItemStack mixinItemStack = ((IItemStack) (Object) instance);
            mixinItemStack.viaFabricPlus$set1_10Count(tag.getByte(ClientsideFixes.ITEM_COUNT_NBT_TAG));
            tag.remove(ClientsideFixes.ITEM_COUNT_NBT_TAG);
            if (tag.isEmpty()) tag = null;
        }

        instance.setNbt(tag);
    }

    @Redirect(method = "writeItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getNbt()Lnet/minecraft/nbt/NbtCompound;"))
    private NbtCompound addViaFabricPlusTag(ItemStack instance) {
        NbtCompound tag = instance.getNbt();

        final IItemStack mixinItemStack = ((IItemStack) (Object) instance);
        if (mixinItemStack.viaFabricPlus$has1_10ViaFabricPlusTag()) {
            if (tag == null) tag = new NbtCompound();
            tag.putByte(ClientsideFixes.ITEM_COUNT_NBT_TAG, (byte) mixinItemStack.viaFabricPlus$get1_10Count());
        }

        return tag;
    }

}
