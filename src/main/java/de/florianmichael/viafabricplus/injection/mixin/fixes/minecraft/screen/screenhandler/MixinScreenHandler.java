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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen.screenhandler;

import de.florianmichael.viafabricplus.injection.access.IScreenHandler;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler implements IScreenHandler {

    @Unique
    private short viaFabricPlus$lastActionId = 0;

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void injectInternalOnSlotClick(int slot, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8) && actionType == SlotActionType.SWAP && clickData == 40) {
            ci.cancel();
        }
    }

    @Override
    public short viaFabricPlus$getAndIncrementLastActionId() {
        return ++viaFabricPlus$lastActionId;
    }
}
