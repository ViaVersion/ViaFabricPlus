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

package com.viaversion.viafabricplus.injection.mixin.features.v1_4_4;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

    /**
     * on 1.4.5 and below(?) if a server receives a container click packet with
     * a negative slot number it will forcefully disconnect the player because
     * the server does not check for the negative slot number and tries to index
     * a list(?) with the negative slot number, causing a disconnect
     * this can happen when you hold shift and spam click on the crafting result slot,
     * especially when crafting multiple different tools
     * this problem does not happen on the actual 1.4.5 and below clients
     */
    @Inject(method = "handleContainerInput", at = @At("HEAD"), cancellable = true)
    private void cancelContainerClickWithNegativeSlotNumber(int containerId, int slotNum, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        if (slotNum < 0 && ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_4tor1_4_5)) {
           ci.cancel();
        }
    }

}
