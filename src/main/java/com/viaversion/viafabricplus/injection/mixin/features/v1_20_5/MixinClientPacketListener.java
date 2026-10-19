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

package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {

    @WrapWithCondition(method = "handleConfigurationStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;sendChatAcknowledgement()V"))
    private boolean dontSendChatAck(ClientPacketListener instance) {
        return ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_5);
    }

    @Redirect(method = "handleOpenBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/BookViewScreen$BookAccess;fromItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/gui/screens/inventory/BookViewScreen$BookAccess;"))
    private BookViewScreen.BookAccess dontOpenWriteableBookScreen(ItemStack itemStack) {
        if (ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_5) || itemStack.is(Items.WRITTEN_BOOK)) {
            return BookViewScreen.BookAccess.fromItem(itemStack);
        } else {
            return null;
        }
    }

}
