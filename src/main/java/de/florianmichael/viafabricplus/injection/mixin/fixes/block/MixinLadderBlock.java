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
package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LadderBlock.class)
public class MixinLadderBlock {

    @Unique
    private static final VoxelShape viafabricplus_east_shape_v1_8_x = Block.createCuboidShape(0, 0, 0, 2, 16, 16);

    @Unique
    private static final VoxelShape viafabricplus_west_shape_v1_8_x = Block.createCuboidShape(14, 0, 0, 16, 16, 16);

    @Unique
    private static final VoxelShape viafabricplus_south_shape_v1_8_x = Block.createCuboidShape(0, 0, 0, 16, 16, 2);

    @Unique
    private static final VoxelShape viafabricplus_north_shape_v1_8_x = Block.createCuboidShape(0, 0, 14, 16, 16, 16);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            switch (state.get(LadderBlock.FACING)) {
                case NORTH -> ci.setReturnValue(viafabricplus_north_shape_v1_8_x);
                case SOUTH -> ci.setReturnValue(viafabricplus_south_shape_v1_8_x);
                case WEST -> ci.setReturnValue(viafabricplus_west_shape_v1_8_x);
                default -> ci.setReturnValue(viafabricplus_east_shape_v1_8_x);
            }
        }
    }
}
