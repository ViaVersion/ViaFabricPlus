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

package com.viaversion.viafabricplus.injection.mixin.features.block.shape;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBlock.class)
public abstract class MixinPistonBlock extends FacingBlock {

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_DOWN_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_UP_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_NORTH_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_SOUTH_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_WEST_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_EAST_SHAPE;

    protected MixinPistonBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_1)) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_1)) {
            if (state.get(PistonBlock.EXTENDED)) {
                return switch (state.get(FACING)) {
                    case DOWN -> EXTENDED_DOWN_SHAPE;
                    case UP -> EXTENDED_UP_SHAPE;
                    case NORTH -> EXTENDED_NORTH_SHAPE;
                    case SOUTH -> EXTENDED_SOUTH_SHAPE;
                    case WEST -> EXTENDED_WEST_SHAPE;
                    case EAST -> EXTENDED_EAST_SHAPE;
                };
            } else {
                return VoxelShapes.fullCube();
            }
        } else {
            return super.getCullingShape(state);
        }
    }

}
