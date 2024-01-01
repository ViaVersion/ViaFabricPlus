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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlock_AbstractBlockState {

    @Shadow
    public abstract Block getBlock();

    @Shadow
    @Final
    private boolean toolRequired;

    /**
     * @author RK_01
     * @reason Change break speed for shulker blocks in < 1.14
     */
    @Overwrite
    public boolean isToolRequired() {
        final Block block = this.getBlock();
        if (block instanceof ShulkerBoxBlock && ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_14)) {
            return true;
        }

        return this.toolRequired;
    }

    @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
    private void changeHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        final var targetVersion = ProtocolHack.getTargetVersion();
        final var block = this.getBlock();

        if (block.equals(Blocks.END_STONE_BRICKS) || block.equals(Blocks.END_STONE_BRICK_SLAB) || block.equals(Blocks.END_STONE_BRICK_STAIRS) || block.equals(Blocks.END_STONE_BRICK_WALL)) {
            if (targetVersion.isOlderThanOrEqualTo(VersionEnum.r1_14_4)) {
                cir.setReturnValue(0.8F);
            }
        } else if (block.equals(Blocks.PISTON) || block.equals(Blocks.STICKY_PISTON) || block.equals(Blocks.PISTON_HEAD)) {
            if (targetVersion.isOlderThanOrEqualTo(VersionEnum.r1_15_2)) {
                cir.setReturnValue(0.5F);
            }
        } else if (block instanceof InfestedBlock) {
            if (targetVersion.isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
                cir.setReturnValue(0.75F);
            } else if (targetVersion.isOlderThanOrEqualTo(VersionEnum.r1_16_4tor1_16_5)) {
                cir.setReturnValue(0F);
            }
        } else if (block.equals(Blocks.OBSIDIAN)) {
            if (targetVersion.isOlderThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
                cir.setReturnValue(10.0F);
            }
        }
    }

}
