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
package de.florianmichael.viafabricplus.injection.mixin.fixes.screen.screenhandler;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerScreenHandler.class)
public abstract class MixinPlayerScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {

    public MixinPlayerScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Redirect(method = "<init>",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler$2;<init>(Lnet/minecraft/screen/PlayerScreenHandler;Lnet/minecraft/inventory/Inventory;III)V")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 0))
    private Slot redirectAddOffhandSlot(PlayerScreenHandler screenHandler, Slot slot) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8))
            return null;
        return addSlot(slot);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "quickMove", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private EquipmentSlot injectTransferSlot(EquipmentSlot slot) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8) && slot == EquipmentSlot.OFFHAND)
            return EquipmentSlot.MAINHAND;
        else
            return slot;
    }
}
