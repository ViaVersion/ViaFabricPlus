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

import com.viaversion.viafabricplus.injection.ViaFabricPlusMixinPlugin;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CauldronBlock.class)
public abstract class MixinCauldronBlock extends AbstractCauldronBlock {

    @Unique
    private static final VoxelShape viaFabricPlus$shape_r1_12_2 = Shapes.join(
        Shapes.block(),
        Block.box(2.0D, 5.0D, 2.0D, 14.0D, 16.0D, 14.0D),
        BooleanOp.ONLY_FIRST
    );

    @Unique
    private static final VoxelShape viaFabricPlus$shape_bedrock = Shapes.or(
        Shapes.box(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
        Shapes.box(0.0, 0.0, 0.0, 0.125, 1.0, 1.0),
        Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.125),
        Shapes.box(1.0 - 0.125, 0.0, 0.0, 1.0, 1.0, 1.0),
        Shapes.box(0.0, 0.0, 1.0 - 0.125, 1.0, 1.0, 1.0)
    );

    public MixinCauldronBlock(Properties settings, CauldronInteraction.InteractionMap behaviorMap) {
        super(settings, behaviorMap);
    }

    @Unique
    private boolean viaFabricPlus$requireOriginalShape;

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (ViaFabricPlusMixinPlugin.MORE_CULLING_PRESENT && viaFabricPlus$requireOriginalShape) {
            viaFabricPlus$requireOriginalShape = false;
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return viaFabricPlus$shape_r1_12_2;
        } else if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return viaFabricPlus$shape_bedrock;
        }
        return super.getShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        // Workaround for https://github.com/ViaVersion/ViaFabricPlus/issues/246
        // MoreCulling is caching the culling shape and doesn't reload it, so we have to force vanilla's shape here.
        viaFabricPlus$requireOriginalShape = true;
        return super.getOcclusionShape(state);
    }

}
