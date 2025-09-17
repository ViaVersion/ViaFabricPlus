/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.block.interaction;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LadderBlock.class)
public abstract class MixinLadderBlock {

    @Inject(method = "canPlaceOn", at = @At(value = "RETURN"), cancellable = true)
    public void fixLadderAttachment(BlockView world, BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        Block block = world.getBlockState(pos).getBlock();
        if (viaFabricPlus$isExceptionForAttachment(block)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean viaFabricPlus$isExceptionForPlacement(Block block) {
        return block instanceof ShulkerBoxBlock
                || block instanceof LeavesBlock
                || block instanceof TrapdoorBlock
                || block instanceof StainedGlassBlock
                || block == Blocks.BEACON
                || block == Blocks.CAULDRON
                || block == Blocks.GLASS
                || block == Blocks.GLOWSTONE
                || block == Blocks.ICE
                || block == Blocks.SEA_LANTERN;
                //|| block == Blocks.STAINED_GLASS;
    }

    @Unique
    private boolean viaFabricPlus$isExceptionForAttachment(Block block) {
        return viaFabricPlus$isExceptionForPlacement(block)
                || block == Blocks.PISTON
                || block == Blocks.STICKY_PISTON
                || block == Blocks.PISTON_HEAD;
    }

}
