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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BambooStalkBlock.class)
public abstract class MixinBambooStalkBlock {

    @Shadow
    @Final
    public static int AGE_THICK_BAMBOO;

    @Shadow
    @Final
    public static IntegerProperty AGE;

    @Unique
    private static final VoxelShape viaFabricPlus$shape_small_bedrock = Block.box(8D, 0.0D, 8D, 10D, 16.0D, 10D);

    @Unique
    private static final VoxelShape viaFabricPlus$shape_large_bedrock = Block.box(8D, 0.0D, 8D, 11D, 16.0D, 11D);

    @Inject(method = {"getShape", "getCollisionShape"}, at = @At("HEAD"), cancellable = true)
    private void fixBambooShape(BlockState blockState, BlockGetter world, BlockPos blockPos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            final VoxelShape voxelShape = blockState.getValue(AGE) == AGE_THICK_BAMBOO ? viaFabricPlus$shape_large_bedrock : viaFabricPlus$shape_small_bedrock;
            cir.setReturnValue(voxelShape.move(blockState.getOffset(blockPos)));
        }
    }

}
