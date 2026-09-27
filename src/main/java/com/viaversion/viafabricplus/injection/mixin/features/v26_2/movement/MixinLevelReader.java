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

package com.viaversion.viafabricplus.injection.mixin.features.v26_2.movement;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelReader.class)
public interface MixinLevelReader {

    @Inject(method = "containsAnyLiquid", at = @At("HEAD"), cancellable = true)
    private void excludeMaxEdgeBlocks(AABB box, CallbackInfoReturnable<Boolean> cir) {
        if (ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(ProtocolVersion.v26_3)) {
            return;
        }

        // 26.3 also checks the blocks touching the max faces of the box
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = Mth.floor(box.minX); x < Mth.ceil(box.maxX); x++) {
            for (int y = Mth.floor(box.minY); y < Mth.ceil(box.maxY); y++) {
                for (int z = Mth.floor(box.minZ); z < Mth.ceil(box.maxZ); z++) {
                    if (!((LevelReader) this).getBlockState(pos.set(x, y, z)).getFluidState().isEmpty()) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
        cir.setReturnValue(false);
    }

}
