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

package com.viaversion.viafabricplus.injection.mixin.features.block.shape;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LanternBlock.class)
public abstract class MixinLanternBlock extends Block {

    @Unique
    private static final VoxelShape viaFabricPlus$shape_bedrock = VoxelShapes.cuboid(0.3125, 0, 0.3125, 0.6875, 0.5, 0.6875);

    @Unique
    private static final VoxelShape viaFabricPlus$shape_hanging_bedrock = VoxelShapes.cuboid(0.3125, 0.125, 0.3125, 0.6875, 0.625, 0.6875);

    @Shadow
    @Final
    public static BooleanProperty HANGING;

    @Shadow
    @Final
    private static VoxelShape STANDING_SHAPE;

    @Shadow
    @Final
    private static VoxelShape HANGING_SHAPE;

    public MixinLanternBlock(final Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("RETURN"), cancellable = true)
    private void modifyCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            cir.setReturnValue(state.get(HANGING) ? viaFabricPlus$shape_hanging_bedrock : viaFabricPlus$shape_bedrock);
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return state.get(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
        } else {
            return super.getCullingShape(state);
        }
    }

}
