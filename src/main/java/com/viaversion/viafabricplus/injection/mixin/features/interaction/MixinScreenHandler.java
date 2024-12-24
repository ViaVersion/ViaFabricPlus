/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.interaction;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.injection.access.IScreenHandler;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScreenHandler.class)
public abstract class MixinScreenHandler implements IScreenHandler {

    @Shadow
    private ItemStack cursorStack;

    @Unique
    private short viaFabricPlus$actionId = 0;

    @Redirect(method = "updateSlotStacks", at = @At(value = "FIELD", target = "Lnet/minecraft/screen/ScreenHandler;cursorStack:Lnet/minecraft/item/ItemStack;"))
    private void preventUpdate(ScreenHandler instance, ItemStack value) {
        if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_17_1)) {
            this.cursorStack = value;
        }
    }

    @Override
    public short viaFabricPlus$getActionId() {
        return viaFabricPlus$actionId;
    }

    @Override
    public short viaFabricPlus$incrementAndGetActionId() {
        return ++viaFabricPlus$actionId;
    }

}
